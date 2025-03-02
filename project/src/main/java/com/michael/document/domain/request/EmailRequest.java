package com.michael.document.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;


@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailRequest {
    @NotEmpty(message = "Email cannot be empty or null")
    @Email(message = "Invalid email address")
    private String email;
}
