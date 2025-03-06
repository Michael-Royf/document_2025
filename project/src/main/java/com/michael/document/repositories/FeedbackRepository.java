package com.michael.document.repositories;

import com.michael.document.entity.DocumentEntity;
import com.michael.document.entity.FeedbackEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeedbackRepository  extends JpaRepository<FeedbackEntity, Long> {
    Optional<FeedbackEntity> findByFeedbackId(String feedbackId);

    Page<FeedbackEntity> findAllByDocumentId(Long documentId, Pageable pageable);

    Page<FeedbackEntity> findFeedbackEntitiesByDocument(DocumentEntity document,  Pageable pageable);

}
