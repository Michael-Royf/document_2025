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
public class DocumentResponse {
    private Long id;
    private String documentId;
    private String name;
    private String description;
    private String uri;
    private long size;
    private String formattedSize;
    private String icon;
    private String extension;
    private String referenceId;
    private Double documentRating;
    private Long downloadCount;
    private int totalLikes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    //
    private String ownerName;
    private String ownerEmail;
    private String ownerPhone;
    private LocalDateTime ownerLastLogin;
    //  private String updaterName;
}
