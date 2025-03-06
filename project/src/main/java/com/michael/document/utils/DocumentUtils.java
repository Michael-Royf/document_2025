package com.michael.document.utils;

import com.michael.document.domain.response.DocumentResponse;
import com.michael.document.entity.DocumentEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class DocumentUtils {
    public static DocumentResponse convertDocumentEntityToResponse(DocumentEntity documentEntity) {
      //  List<FeedbackEntity> feedbackEntities = documentEntity.getFeedbacks();
        return DocumentResponse.builder()
                .id(documentEntity.getId())
                .documentId(documentEntity.getDocumentId())
                .name(documentEntity.getName())
                .description(documentEntity.getDescription())
                .uri(documentEntity.getUri())
                .size(documentEntity.getSize())
                .formattedSize(documentEntity.getFormattedSize())
                .icon(documentEntity.getIcon())
             //   .documentRating(documentEntity.getRate())
                .extension(documentEntity.getExtension())
                .referenceId(documentEntity.getReferenceId())
                .createdAt(documentEntity.getCreatedAt())
                .updatedAt(documentEntity.getUpdatedAt())
                .ownerName(documentEntity.getOwner().getFullName())
                .ownerEmail(documentEntity.getOwner().getEmail())
                .ownerPhone(documentEntity.getOwner().getPhone())
                .ownerLastLogin(documentEntity.getOwner().getLastLogin())
            //    .feedbackResponses(mappingListFeedbackEntityToFeedbackResponse(feedbackEntities))
               // .totalLikes(documentEntity.getTotalLikes())
                //.downloadCount(documentEntity.getDownloadCount())
                .build();
    }

    public static Page<DocumentResponse> convertToDocumentResponsePage(Page<DocumentEntity> documentEntities,
                                                                 Pageable pageable) {
        List<DocumentResponse> documentResponses = documentEntities
                .stream()
                .map(DocumentUtils::convertDocumentEntityToResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(documentResponses, pageable, documentEntities.getTotalElements());
    }


    public static String getDocumentUri(String filename) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(String.format("/document/%s", filename)).toUriString();
    }

    public static String setIcon(String fileExtension) {
        var extension = StringUtils.trimAllWhitespace(fileExtension);
        if (extension.equalsIgnoreCase("DOC") || extension.equalsIgnoreCase("DOCX")) {
            return "https://htmlstream.com/preview/front-dashboard-v2.1.1/assets/svg/brands/word-icon.svg";
        }
        if (extension.equalsIgnoreCase("XLS") || extension.equalsIgnoreCase("XLSX")) {
            return "https://htmlstream.com/preview/front-dashboard-v2.1.1/assets/svg/brands/excel-icon.svg";
        }
        if (extension.equalsIgnoreCase("PDF")) {
            return "https://htmlstream.com/preview/front-dashboard-v2.1.1/assets/svg/brands/pdf-icon.svg";
        } else {
            return "https://htmlstream.com/preview/front-dashboard-v2.1.1/assets/svg/brands/word-icon.svg";
        }
    }

}
