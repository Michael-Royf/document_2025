package com.michael.document.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResetPasswordRequest {
    @NotEmpty(message = "User ID cannot be empty or null")
    private String userId;
    @NotEmpty(message = "Password cannot be empty or null")
    private String newPassword;
    @NotEmpty(message = "Confirm password cannot be empty or null")
    private String confirmationPassword;
}
