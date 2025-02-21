package com.michael.document.service.impl;

import com.michael.document.entity.ConfirmationEntity;
import com.michael.document.entity.CredentialEntity;
import com.michael.document.entity.RoleEntity;
import com.michael.document.entity.UserEntity;
import com.michael.document.enumeration.Authority;
import com.michael.document.enumeration.EventType;
import com.michael.document.event.UserEvent;
import com.michael.document.exceptions.NotFoundException;
import com.michael.document.payload.request.RegistrationRequest;
import com.michael.document.repository.ConfirmationRepository;
import com.michael.document.repository.CredentialRepository;
import com.michael.document.repository.RoleRepository;
import com.michael.document.repository.UserRepository;
import com.michael.document.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CredentialRepository credentialRepository;
    private final ConfirmationRepository confirmationRepository;
    private final ApplicationEventPublisher publisher;

    @Override
    public void createUser(RegistrationRequest request) throws IOException {
        var userEntity = createNewUser(request);
        userRepository.save(userEntity);
        var credentialEntity = new CredentialEntity(request.getPassword(), userEntity);
        credentialRepository.save(credentialEntity);
        var confirmationEntity = new ConfirmationEntity(userEntity);
        confirmationRepository.save(confirmationEntity);
        publisher.publishEvent(new UserEvent(userEntity, EventType.REGISTRATION, Map.of("key", confirmationEntity.getKey())));
    }

    @Override
    public void verifyAccountKey(String key) {
        var confirmationEntity = getUserConfirmation(key);
        var userEntity = getUserEntityByEmail(confirmationEntity.getUserEntity().getEmail());
        userEntity.setEnabled(true);
        userRepository.save(userEntity);
        confirmationRepository.delete(confirmationEntity);

    }

    private UserEntity getUserEntityByEmail(String email) {
        return userRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }


    private ConfirmationEntity getUserConfirmation(String key) {
        return confirmationRepository.findByKey(key)
                .orElseThrow(() -> new NotFoundException("CONFIRMATION_INFORMATION_NOT_FOUND"));
    }


    private ConfirmationEntity getUserConfirmation(UserEntity userEntity) {
        return confirmationRepository.findByUserEntity(userEntity)
                .orElse(null);
    }

    public RoleEntity getRoleName(String name) {
        return roleRepository.findByName(name).orElseThrow(() ->
                new NotFoundException("No role found  by name " + name));
    }


    private UserEntity createNewUser(RegistrationRequest request) {
        var role = getRoleName(Authority.USER.name());
        return createUserEntity(request.getFirstName(), request.getLastName(), request.getEmail(), role);
    }


    private UserEntity createUserEntity(String firstName, String lastName, String email, RoleEntity role) {
        return UserEntity.builder()
                .userId(UUID.randomUUID().toString())
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
                .imageUrl("https://cdn-icons-png.flaticon.com/512/149/149071.png")
                .build();
    }


    private String firstLetterUpper(String world) {
        return Arrays.stream(world.split(" "))
                .filter(word -> !word.isEmpty())
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
    

}
