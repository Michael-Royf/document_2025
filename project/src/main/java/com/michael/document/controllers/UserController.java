package com.michael.document.controllers;

import com.michael.document.domain.User;
import com.michael.document.domain.request.*;
import com.michael.document.domain.response.Response;
import com.michael.document.enumerations.TokenType;
import com.michael.document.handler.ApiLogoutHandler;
import com.michael.document.services.JwtService;
import com.michael.document.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import static com.michael.document.utils.ResponseUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final ApiLogoutHandler apiLogoutHandler;


    @PostMapping("/register")
    public ResponseEntity<Response> createUser(@RequestBody @Valid RegistrationRequest newUser,
                                               HttpServletRequest request) throws IOException {
        userService.createUser(newUser);
        return ResponseEntity.created(URI.create(""))
                .body(getResponse(
                        request,
                        emptyMap(),
                        "Account created. Check your email to enable your account",
                        CREATED));
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
                        OK));
    }


    @PatchMapping("/mfa/setup")
    public ResponseEntity<Response> setupMfa(@AuthenticationPrincipal User userPrincipal,
                                             HttpServletRequest request) {
        var user = userService.setUpMfa(userPrincipal.getId());
        return ResponseEntity.ok().body(getResponse(
                request,
                Map.of("user", user),
                "MFA set up successfully",
                OK));
    }


    @PatchMapping("/mfa/cancel")
    public ResponseEntity<Response> cancelMfa(@AuthenticationPrincipal User userPrincipal,
                                              HttpServletRequest request) {

        var user = userService.cancelMfa(userPrincipal.getId());
        return ResponseEntity.ok().body(getResponse(
                request,
                Map.of("user", user),
                "MFA cancel successfully",
                OK));
    }


    @PostMapping("/verify/qrcode")
    public ResponseEntity<Response> verifyQrCode(@RequestBody @Valid QrCodeRequest qrCodeRequest,
                                                 HttpServletResponse response,
                                                 HttpServletRequest request) {
        var user = userService.verifyQrCode(qrCodeRequest.getUserId(), qrCodeRequest.getQrCode());
        jwtService.addCookie(response, user, TokenType.ACCESS);
        jwtService.addCookie(response, user, TokenType.REFRESH);
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        Map.of("user", user),
                        "QR code verified.",
                        OK));
    }


    //reset password when user not logged
    @PostMapping("/reset_password")
    public ResponseEntity<Response> resetPassword(@RequestBody @Valid EmailRequest emailRequest,
                                                  HttpServletRequest request) {
        userService.resetPassword(emailRequest.getEmail());
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        emptyMap(),
                        "We have sent a password reset link to your email.",
                        OK));
    }

    @GetMapping("/verify/password")
    public ResponseEntity<Response> verifyPassword(@RequestParam("key") String key,
                                                   HttpServletRequest request) {

        var user = userService.verifyPasswordKey(key);
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        Map.of("user", user),
                        "Enter new password",
                        OK));
    }

    @PostMapping("/reset_password/reset")
    public ResponseEntity<Response> doResetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest,
                                                    HttpServletRequest request) {
        userService.updatePassword(resetPasswordRequest);
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        emptyMap(),
                        "Password reset successfully.",
                        OK));
    }

    //reset password when user is logged
    @PatchMapping("/update_password")
    public ResponseEntity<Response> updatePassword(@AuthenticationPrincipal User user,
                                                   @RequestBody @Valid UpdatePasswordRequest updatePasswordRequest,
                                                   HttpServletRequest request) {
        userService.updatePassword(user.getUserId(), updatePasswordRequest);
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        emptyMap(),
                        "Password updated successfully.",
                        OK));
    }


    @GetMapping("/profile")
    public ResponseEntity<Response> profile(@AuthenticationPrincipal User userPrincipal,
                                            HttpServletRequest request) {
        var user = userService.getUserByUserId(userPrincipal.getUserId());
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        Map.of("user", user),
                        "Profile retrieved.",
                        OK));
    }

    @PatchMapping("/update")
    public ResponseEntity<Response> updateUserProfile(@AuthenticationPrincipal User userPrincipal,
                                                      @RequestBody RegistrationRequest registrationRequest,
                                                      HttpServletRequest request) {
        var user = userService.updateUser(userPrincipal.getUserId(), registrationRequest);
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        Map.of("user", user),
                        "User updated successfully.",
                        OK));
    }

    @PatchMapping("/update_role")
    public ResponseEntity<Response> updateUserRole(@AuthenticationPrincipal User userPrincipal,
                                                   @RequestBody RoleRequest roleRequest,
                                                   HttpServletRequest request) {
        userService.updateRole(userPrincipal.getUserId(), roleRequest);
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        emptyMap(),
                        "Role update successfully.",
                        OK));
    }


    @PatchMapping("/toggle_account_expired")
    public ResponseEntity<Response> toggleAccountExpired(@AuthenticationPrincipal User user,
                                                         HttpServletRequest request) {
        userService.toggleAccountExpired(user.getUserId());
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        emptyMap(),
                        "Account updated successfully.",
                        OK));
    }

    @PatchMapping("/toggle_account_locked")
    public ResponseEntity<Response> toggleAccountLocked(@AuthenticationPrincipal User user,
                                                        HttpServletRequest request) {
        userService.toggleAccountLocked(user.getUserId());
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        emptyMap(),
                        "Account updated successfully.",
                        OK));
    }

    @PatchMapping("/toggle_account_enabled")
    public ResponseEntity<Response> toggleAccountEnabled(@AuthenticationPrincipal User user,
                                                         HttpServletRequest request) {
        userService.toggleAccountEnabled(user.getUserId());
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        emptyMap(),
                        "Account updated successfully.",
                        OK));
    }

    @PatchMapping("/toggle_credentials_expired")
    public ResponseEntity<Response> toggleCredentialsExpired(@AuthenticationPrincipal User user,
                                                             HttpServletRequest request) {
        userService.toggleCredentialsExpired(user.getUserId());
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        emptyMap(),
                        "Account updated successfully.",
                        OK));
    }


    @PostMapping("/logout")
    public ResponseEntity<Response> logout(HttpServletResponse response,
                                           HttpServletRequest request,
                                           Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        apiLogoutHandler.logout(request, response, authentication);
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        emptyMap(),
                        "You have logged out successfully. Please log in again.",
                        OK));
    }


//    @GetMapping("/resend/verification")
//    public ResponseEntity<Response> resendVerificationEmail(@RequestParam("email") String email,
//                                                        HttpServletRequest request) {


}
