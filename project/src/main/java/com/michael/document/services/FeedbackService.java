package com.michael.document.services;

import com.michael.document.domain.request.FeedbackRequest;
import com.michael.document.domain.response.FeedbackResponse;
import org.springframework.data.domain.Page;

public interface FeedbackService {
    FeedbackResponse createFeedback(String userId, FeedbackRequest feedbackRequest);

    Page<FeedbackResponse> findAllFeedbacksByDocument(String documentId, int pageNo, int pageSize, String sortBy, String sortDir);

    FeedbackResponse getFeedbackById(String feedbackId);

    FeedbackResponse updateFeedback(String feedbackId, String userId, FeedbackRequest feedbackRequest);

    void deleteFeedback(String feedbackId, String userId);
}
