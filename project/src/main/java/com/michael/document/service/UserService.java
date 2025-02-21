package com.michael.document.service;

import com.michael.document.payload.request.RegistrationRequest;

import java.io.IOException;

public interface UserService {
    void createUser(RegistrationRequest request) throws IOException;

    void verifyAccountKey(String key);
}
