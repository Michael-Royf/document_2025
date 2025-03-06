package com.michael.document.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.michael.document.validations.PasswordMatches;
import com.michael.document.validations.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@PasswordMatches
public class RegistrationRequest {
    @NotEmpty(message = "Username cannot be empty or null")
    private String username;
    @NotEmpty(message = "First name cannot be empty or null")
    private String firstName;
    @NotEmpty(message = "Last name cannot be empty or null")
    private String lastName;
    @NotEmpty(message = "Email cannot be empty or null")
    @Email(message = "Invalid email address")
    private String email;
    @ValidPassword
    @NotEmpty(message = "Password cannot be empty or null")
    private String password;
    @NotEmpty(message = "Confirmation password cannot be empty or null")
    private String confirmationPassword;
    private String bio;
    private String phone;
}
