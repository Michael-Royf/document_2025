package com.michael.document.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateDocumentRequest {
    @NotEmpty(message = "Document ID cannot be empty or null")
    private String documentId;
    @NotEmpty(message = "Name cannot be empty or null")
    private String name;
    @NotEmpty(message = "Description cannot be empty or null")
    private String description;
}
