package com.michael.document.services;

import com.michael.document.domain.User;
import com.michael.document.domain.request.RegistrationRequest;
import com.michael.document.domain.request.ResetPasswordRequest;
import com.michael.document.domain.request.RoleRequest;
import com.michael.document.domain.request.UpdatePasswordRequest;
import com.michael.document.entity.CredentialEntity;
import com.michael.document.entity.UserEntity;
import com.michael.document.enumerations.LoginType;


import java.io.IOException;

public interface UserService {
    void saveUserEntity(UserEntity userEntity);

    void createUser(RegistrationRequest request) throws IOException;

    void verifyAccountKey(String key);

    void updateLoginAttempts(String email, LoginType loginType);

    User getUserByUserId(String userId);

    User getUserByEmail(String email);

    User getUserByUsername(String username);

    UserEntity getUserEntityByUserId(String userId);

    CredentialEntity getUserCredentialById(Long userId);

    User setUpMfa(Long userId);

    User cancelMfa(Long userId);

    User verifyQrCode(String userId, String qrCode);

    void resetPassword(String email);

    User verifyPasswordKey(String key);

    void updatePassword(ResetPasswordRequest resetPasswordRequest);

    void updatePassword(String userId, UpdatePasswordRequest updatePasswordRequest);

    User updateUser(String userId, RegistrationRequest registrationRequest);

    void updateRole(String userId, RoleRequest roleRequest);

    //
    void toggleAccountExpired(String userId);

    void toggleAccountLocked(String userId);

    void toggleAccountEnabled(String userId);

    void toggleCredentialsExpired(String userId);

}
