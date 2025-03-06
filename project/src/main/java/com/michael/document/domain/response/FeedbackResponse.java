package com.michael.document.domain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class FeedbackResponse {
    private String feedbackId;
    private String comment;
    private String ownerFullName;
    private LocalDateTime createdAt;
}
