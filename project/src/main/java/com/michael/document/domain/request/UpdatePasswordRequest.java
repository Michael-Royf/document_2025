package com.michael.document.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.michael.document.validations.PasswordMatches;
import com.michael.document.validations.ValidPassword;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@PasswordMatches
public class UpdatePasswordRequest {
    @NotEmpty(message = "Current password cannot be empty or null")
    private String currentPassword;
    @ValidPassword
    @NotEmpty(message = "New password cannot be empty or null")
    private String newPassword;
    @NotEmpty(message = "Confirmation password cannot be empty or null")
    private String confirmationPassword;
}
