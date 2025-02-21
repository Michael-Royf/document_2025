package com.michael.document.controller;

import com.michael.document.payload.request.RegistrationRequest;
import com.michael.document.payload.response.Response;
import com.michael.document.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;

import static com.michael.document.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PostMapping("/register")
    public ResponseEntity<Response> createUser(@RequestBody @Valid RegistrationRequest newUser,
                                               HttpServletRequest request) throws IOException {
        userService.createUser(newUser);
        return ResponseEntity.created(URI.create(""))
                .body(getResponse(
                        request,
                        emptyMap(),
                        "Account created. Check your email to enable your account",
                        HttpStatus.CREATED));
    }


    @GetMapping("/verify/account")
    public ResponseEntity<Response> verifyAccount(@RequestParam("key") String key,
                                                  HttpServletRequest request) {
        userService.verifyAccountKey(key);
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        emptyMap(),
                        "Account verified.",
                        HttpStatus.OK));
    }


}
