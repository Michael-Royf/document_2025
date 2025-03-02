package com.michael.document.services;

import com.michael.document.domain.User;
import com.michael.document.domain.request.RegistrationRequest;
import com.michael.document.domain.request.ResetPasswordRequest;
import com.michael.document.entity.CredentialEntity;
import com.michael.document.enumerations.LoginType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

import java.io.IOException;

public interface UserService {
    void createUser(RegistrationRequest request) throws IOException;

    void verifyAccountKey(String key);

    void updateLoginAttempts(String email, LoginType loginType);

    User getUserByUserId(String userId);

    User getUserByEmail(String email);

    CredentialEntity getUserCredentialById(Long userId);

    User setUpMfa(Long userId);

    User cancelMfa(Long userId);

    User verifyQrCode(String userId, String qrCode);

    void resetPassword(String email);

    User verifyPasswordKey(String key);

    void updatePassword(ResetPasswordRequest resetPasswordRequest);
}
