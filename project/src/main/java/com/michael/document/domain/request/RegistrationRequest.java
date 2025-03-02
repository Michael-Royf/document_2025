package com.michael.document.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationRequest {
    @NotEmpty(message = "First name cannot be empty or null")
    private String firstName;
    @NotEmpty(message = "Last name cannot be empty or null")
    private String lastName;
    @NotEmpty(message = "Email cannot be empty or null")
    @Email(message = "Invalid email address")
    private String email;
    @NotEmpty(message = "Password cannot be empty or null")
    private String password;

//    @NotEmpty(message = "Password cannot be empty or null")
//    private String confirmationPassword;

    private String bio;
    private String phone;
}
