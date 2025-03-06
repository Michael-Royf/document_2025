package com.michael.document.services.impl;

import com.michael.document.cache.CacheStore;
import com.michael.document.domain.User;
import com.michael.document.domain.request.ResetPasswordRequest;
import com.michael.document.domain.request.RoleRequest;
import com.michael.document.domain.request.UpdatePasswordRequest;
import com.michael.document.entity.ConfirmationEntity;
import com.michael.document.entity.CredentialEntity;
import com.michael.document.entity.RoleEntity;
import com.michael.document.entity.UserEntity;
import com.michael.document.entity.base.RequestContext;
import com.michael.document.enumerations.Authority;
import com.michael.document.enumerations.EventType;
import com.michael.document.enumerations.LoginType;
import com.michael.document.event.UserEvent;
import com.michael.document.exception.payload.ApiException;
import com.michael.document.exception.payload.ExistsException;
import com.michael.document.exception.payload.NotFoundException;
import com.michael.document.domain.request.RegistrationRequest;
import com.michael.document.repositories.ConfirmationRepository;
import com.michael.document.repositories.CredentialRepository;
import com.michael.document.repositories.RoleRepository;
import com.michael.document.repositories.UserRepository;
import com.michael.document.services.AvatarService;
import com.michael.document.services.UserService;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.michael.document.constants.AppConstant.*;
import static com.michael.document.utils.UserUtils.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    public static final String USER_IS_DISABLE = "User is disable";
    public static final String ACCOUNT_IS_EXPIRED = "Account is expired";
    public static final String ACCOUNT_IS_LOCKED = "Account is locked";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CredentialRepository credentialRepository;
    private final ConfirmationRepository confirmationRepository;
    private final ApplicationEventPublisher publisher;
    private final CacheStore<String, Integer> userCache;
    private final PasswordEncoder passwordEncoder;
    private final AvatarService avatarService;

    @Override
    public void saveUserEntity(UserEntity userEntity) {
        userRepository.save(userEntity);
    }

    @Override
    public void createUser(RegistrationRequest request) throws IOException {
        validateNewUsernameAndEmail(
                StringUtils.EMPTY,
                request.getUsername(),
                request.getEmail());

        var userEntity = createNewUser(request);
        userRepository.save(userEntity);
        avatarService.saveTempAvatar(userEntity);
        var credentialEntity = new CredentialEntity(passwordEncoder.encode(request.getPassword()), userEntity);
        credentialRepository.save(credentialEntity);
        var confirmationEntity = new ConfirmationEntity(userEntity);
        confirmationRepository.save(confirmationEntity);
        publisher.publishEvent(new UserEvent(userEntity, EventType.REGISTRATION, Map.of("key", confirmationEntity.getKey())));
    }

    @Override
    public void verifyAccountKey(String key) {
        var confirmationEntity = getUserConfirmationEntity(key);
        var userEntity = getUserEntityByEmail(confirmationEntity.getUserEntity().getEmail());
        userEntity.setEnabled(true);
        userRepository.save(userEntity);
        confirmationRepository.delete(confirmationEntity);

    }
    //TODO:  автоматическая разблокировка.  Implement password hashing and salting

    @Override
    public void updateLoginAttempts(String email, LoginType loginType) {
        var userEntity = getUserEntityByEmail(email);
        RequestContext.setUserId(userEntity.getId());
        switch (loginType) {
            case LOGIN_ATTEMPT -> {
                if (userCache.get(userEntity.getEmail()) == null) {
                    userEntity.setLoginAttempts(0);
                    userEntity.setAccountNonLocked(true);
                }
                userEntity.setLoginAttempts(userEntity.getLoginAttempts() + 1);
                userCache.put(userEntity.getEmail(), userEntity.getLoginAttempts());
                if (userCache.get(userEntity.getEmail()) > 5) {
                    userEntity.setAccountNonLocked(false);
                }
            }
            case LOGIN_SUCCESS -> {
                userEntity.setAccountNonLocked(true);
                userEntity.setLoginAttempts(0);
                userEntity.setLastLogin(LocalDateTime.now());
                userCache.evict(userEntity.getEmail());
            }
        }
        userRepository.save(userEntity);
    }

    @Override
    public User getUserByUserId(String userId) {
        UserEntity userEntity = getUserEntityByUserId(userId);
        return fromUserEntity(userEntity, userEntity.getRoles(),
                getUserCredentialById(userEntity.getId()));
    }

    @Override
    public UserEntity getUserEntityByUserId(String userId) {
        return userRepository.findUserEntityByUserId(userId)
                .orElseThrow(() -> new NotFoundException(NO_USER_FOUND_BY_ID));
    }

    @Override
    public User getUserByEmail(String email) {
        var userEntity = getUserEntityByEmail(email);
        return fromUserEntity(userEntity, userEntity.getRoles(),
                getUserCredentialById(userEntity.getId()));
    }

    @Override
    public User getUserByUsername(String username) {
        var userEntity = getUserEntityByUsername(username);
        return fromUserEntity(userEntity, userEntity.getRoles(),
                getUserCredentialById(userEntity.getId()));
    }


    @Override
    public CredentialEntity getUserCredentialById(Long userId) {
        return credentialRepository.getCredentialEntityByUserEntityId(userId)
                .orElseThrow(() -> new NotFoundException("USER_CREDENTIAL_NOT_FOUND"));
    }

    @Override
    public User setUpMfa(Long userId) {
        var userEntity = getUserEntityByUserId(userId);
        var codeSecret = qrCodeSecret.get();
        userEntity.setQrCodeImageUri(qrCodeImageUri.apply(userEntity.getEmail(), codeSecret));
        userEntity.setQrCodeSecret(codeSecret);
        userEntity.setMfa(true);
        userRepository.save(userEntity);
        return fromUserEntity(userEntity, userEntity.getRoles(),
                getUserCredentialById(userEntity.getId()));
    }

    @Override
    public User cancelMfa(Long userId) {
        var userEntity = getUserEntityByUserId(userId);
        userEntity.setMfa(false);
        userEntity.setQrCodeImageUri(EMPTY);
        userEntity.setQrCodeSecret(EMPTY);
        userRepository.save(userEntity);
        return fromUserEntity(userEntity, userEntity.getRoles(),
                getUserCredentialById(userEntity.getId()));
    }

    @Override
    public User verifyQrCode(String userId, String qrCode) {
        var userEntity = getUserEntityByUserId(userId);
        verifyCode(qrCode, userEntity.getQrCodeSecret());
        return fromUserEntity(userEntity, userEntity.getRoles(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public void resetPassword(String email) {
        var userEntity = getUserEntityByEmail(email);
        var confirmation = getUserConfirmationEntity(userEntity);
        if (confirmation != null) {
            publisher.publishEvent(new UserEvent(userEntity, EventType.RESET_PASSWORD, Map.of("key", confirmation.getKey())));
        } else {
            var confirmationEntity = new ConfirmationEntity(userEntity);
            confirmationRepository.save(confirmationEntity);
            publisher.publishEvent(new UserEvent(userEntity, EventType.RESET_PASSWORD, Map.of("key", confirmationEntity.getKey())));
        }

    }

    @Override
    public User verifyPasswordKey(String key) {
        var confirmationEntity = getUserConfirmationEntity(key);
        var userEntity = getUserEntityByEmail(confirmationEntity.getUserEntity().getEmail());
        verifyAccountStatus(userEntity);
        confirmationRepository.delete(confirmationEntity);
        return fromUserEntity(userEntity, userEntity.getRoles(),
                getUserCredentialById(userEntity.getId()));
    }

    @Override
    public void updatePassword(ResetPasswordRequest resetPasswordRequest) {
        var user = getUserByUserId(resetPasswordRequest.getUserId());
        if (!resetPasswordRequest.getNewPassword().equals(resetPasswordRequest.getConfirmationPassword())) {
            throw new ApiException("Passwords do not match, Please try again.");
        }
        var credentials = getUserCredentialById(user.getId());
        credentials.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        credentialRepository.save(credentials);
    }

    @Override
    public void updatePassword(String userId, UpdatePasswordRequest updatePasswordRequest) {
        var user = getUserEntityByUserId(userId);
        verifyAccountStatus(user);
        var credentials = getUserCredentialById(user.getId());
        if (!passwordEncoder.matches(updatePasswordRequest.getCurrentPassword(), credentials.getPassword())) {
            throw new ApiException(EXISTING_PASSWORD_INCORRECT);
        }
        credentials.setPassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));
        credentialRepository.save(credentials);
    }

    @Override
    public User updateUser(String userId, RegistrationRequest registrationRequest) {
        var userEntity = getUserEntityByUserId(userId);
        validateNewUsernameAndEmail(
                userEntity.getUsername(),
                registrationRequest.getUsername(),
                registrationRequest.getEmail());
        userEntity.setFirstName(registrationRequest.getFirstName());
        userEntity.setLastName(registrationRequest.getLastName());
        userEntity.setUsername(registrationRequest.getUsername());
        userEntity.setEmail(registrationRequest.getEmail());
        userEntity.setBio(registrationRequest.getBio());
        userEntity.setPhone(registrationRequest.getPhone());//TODO:  Implement phone verification
        userRepository.save(userEntity);
        return fromUserEntity(userEntity, userEntity.getRoles(),
                getUserCredentialById(userEntity.getId()));
    }

    //TODO: Для админов!
    @Override
    public void updateRole(String userId, RoleRequest roleRequest) {
        var userEntity = getUserEntityByUserId(userId);
        userEntity.setRoles(getRoleName(roleRequest.getRole()));
        userRepository.save(userEntity);
    }

    @Override
    public void toggleAccountExpired(String userId) {
        var userEntity = getUserEntityByUserId(userId);
        userEntity.setAccountNonExpired(!userEntity.isAccountNonExpired());
        userRepository.save(userEntity);
    }

    @Override
    public void toggleAccountLocked(String userId) {
        var userEntity = getUserEntityByUserId(userId);
        userEntity.setAccountNonLocked(!userEntity.isAccountNonLocked());
        userRepository.save(userEntity);
    }

    @Override
    public void toggleAccountEnabled(String userId) {
        var userEntity = getUserEntityByUserId(userId);
        userEntity.setEnabled(!userEntity.isEnabled());
        userRepository.save(userEntity);
    }

    @Override
    public void toggleCredentialsExpired(String userId) {
        var userEntity = getUserEntityByUserId(userId);
        var credentials = getUserCredentialById(userEntity.getId());
        //TODO: FIX
        credentials.setUpdatedAt(LocalDateTime.of(1995, 7, 12, 11, 11));
        //        if (credentials.getUpdatedAt().plusDays(90).isAfter(LocalDateTime.now())) {
//            credentials.setUpdatedAt(LocalDateTime.now());
//        } else {
//            credentials.setUpdatedAt(LocalDateTime.of(1995, 7, 12, 11, 11));
//        }
        userRepository.save(userEntity);
    }

    private void verifyAccountStatus(UserEntity userEntity) {
        if (!userEntity.isEnabled()) {
            throw new ApiException(USER_IS_DISABLE);
        }
        if (!userEntity.isAccountNonExpired()) {
            throw new ApiException(ACCOUNT_IS_EXPIRED);
        }
        if (!userEntity.isAccountNonLocked()) {
            throw new ApiException(ACCOUNT_IS_LOCKED);
        }
    }


    private boolean verifyCode(String qrCode, String qrCodeSecret) {
        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        CodeVerifier codeVerifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        if (codeVerifier.isValidCode(qrCodeSecret, qrCode)) {
            return true;
        } else {
            throw new ApiException("Invalid QR code. Please try again.");
        }

    }


    private UserEntity getUserEntityByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("NO USER FOUND_BY_USER_ID"));
    }


    private ConfirmationEntity getUserConfirmationEntity(String key) {
        return confirmationRepository.findByKey(key)
                .orElseThrow(() -> new NotFoundException("CONFIRMATION_INFORMATION_NOT_FOUND"));
    }


    private ConfirmationEntity getUserConfirmationEntity(UserEntity userEntity) {
        return confirmationRepository.findByUserEntity(userEntity)
                .orElse(null);
    }


    private UserEntity createNewUser(RegistrationRequest request) {
        var role = getRoleName(Authority.USER.name());
        return createUserEntity(request.getUsername(), request.getFirstName(), request.getLastName(), request.getEmail(), role);
    }

    public RoleEntity getRoleName(String name) {
        return roleRepository.findByName(name).orElseThrow(() ->
                new NotFoundException("No role found  by name " + name));
    }


    private UserEntity createUserEntity(String username, String firstName, String lastName, String email, RoleEntity role) {
        return UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .username(username)
                .firstName(firstLetterUpper(firstName))
                .lastName(firstLetterUpper(lastName))
                .email(email)
                .roles(role)
                .lastLogin(LocalDateTime.now())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .enabled(false)
                .mfa(false)
                .enabled(false)
                .loginAttempts(0)
                .qrCodeSecret(EMPTY)
                .phone(EMPTY)//TODO: fix
                .bio(EMPTY)
            //    .avatarUrl("https://cdn-icons-png.flaticon.com/512/149/149071.png")
                .build();
    }


    private String firstLetterUpper(String world) {
        return Arrays.stream(world.split(" "))
                .filter(word -> !word.isEmpty())
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    private void validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) {
        UserEntity userByNewUsername = findOptionalUserByUsername(newUsername).orElse(null);
        UserEntity userByNewEmail = findOptionalUserByEmail(newEmail).orElse(null);
        if (StringUtils.isNotBlank(currentUsername)) {
            UserEntity currentUser = findOptionalUserByUsername(currentUsername).orElse(null);
            if (currentUser == null) {
                throw new NotFoundException(String.format(NO_USER_FOUND_BY_USERNAME, currentUsername));
            }
            if (userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
                throw new ExistsException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
                throw new ExistsException(EMAIL_ALREADY_EXISTS);
            }
        } else {
            if (userByNewUsername != null) {
                throw new ExistsException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null) {
                throw new ExistsException(EMAIL_ALREADY_EXISTS);
            }
        }
    }

//    private void validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) {
//        UserEntity currentUser = null;
//
//        if (StringUtils.isNotBlank(currentUsername)) {
//            currentUser = findOptionalUserByUsername(currentUsername).orElse(null);
//            if (currentUser == null) {
//                throw new NotFoundException(String.format(NO_USER_FOUND_BY_USERNAME, currentUsername));
//            }
//        }
//
//        // Проверяем, существуют ли пользователи с новым username и email
//        UserEntity userByNewUsername = findOptionalUserByUsername(newUsername).orElse(null);
//        UserEntity userByNewEmail = findOptionalUserByEmail(newEmail).orElse(null);
//
//        // Проверяем, не совпадают ли ID текущего пользователя с пользователем с новым username
//        if (isUsernameTakenByAnotherUser(userByNewUsername, currentUser)) {
//            throw new ExistsException(USERNAME_ALREADY_EXISTS);
//        }
//
//        // Проверяем, не совпадают ли ID текущего пользователя с пользователем с новым email
//        if (isEmailTakenByAnotherUser(userByNewEmail, currentUser)) {
//            throw new ExistsException(EMAIL_ALREADY_EXISTS);
//        }
//    }
//
//    private boolean isUsernameTakenByAnotherUser(UserEntity userByNewUsername, UserEntity currentUser) {
//        return userByNewUsername != null && (currentUser == null || !currentUser.getId().equals(userByNewUsername.getId()));
//    }
//
//    private boolean isEmailTakenByAnotherUser(UserEntity userByNewEmail, UserEntity currentUser) {
//        return userByNewEmail != null && (currentUser == null || !currentUser.getId().equals(userByNewEmail.getId()));
//    }


    private UserEntity getUserEntityByEmail(String email) {
        return findOptionalUserByEmail(email)
                .orElseThrow(() -> new NotFoundException(NO_USER_FOUND_BY_EMAIL));
    }

    private UserEntity getUserEntityByUsername(String username) {
        return findOptionalUserByUsername(username)
                .orElseThrow(() -> new NotFoundException(NO_USER_FOUND_BY_USERNAME));
    }

    private Optional<UserEntity> findOptionalUserByEmail(String email) {
        return userRepository.findUserEntityByEmail(email);
    }

    private Optional<UserEntity> findOptionalUserByUsername(String username) {
        return userRepository.findUserEntityByUsername(username);
    }
}
