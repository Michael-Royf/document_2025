package com.michael.document.services.impl;

import com.michael.document.domain.request.FeedbackRequest;
import com.michael.document.domain.response.FeedbackResponse;
import com.michael.document.entity.DocumentEntity;
import com.michael.document.entity.FeedbackEntity;
import com.michael.document.entity.UserEntity;
import com.michael.document.exception.payload.ApiException;
import com.michael.document.exception.payload.NotFoundException;
import com.michael.document.repositories.FeedbackRepository;
import com.michael.document.services.DocumentService;
import com.michael.document.services.FeedbackService;
import com.michael.document.services.UserService;
import com.michael.document.utils.FeedbackUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.michael.document.utils.FeedbackUtils.mapFeedbackEntityToFeedbackResponse;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final DocumentService documentService;
    private final UserService userService;


    @Override
    public FeedbackResponse createFeedback(String userId, FeedbackRequest feedbackRequest) {
        var userEntity = userService.getUserEntityByUserId(userId);
        var documentEntity = documentService.getDocumentEntityByDocumentId(feedbackRequest.getDocumentId());
        var feedback = buildFeedbackEntity(feedbackRequest, documentEntity, userEntity);
        feedbackRepository.saveAndFlush(feedback);
        return mapFeedbackEntityToFeedbackResponse(feedback);
    }


    @Override
    public Page<FeedbackResponse> findAllFeedbacksByDocument(String documentId, int pageNo,
                                                             int pageSize, String sortBy,
                                                             String sortDir) {
        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        var documentEntity = documentService.getDocumentEntityByDocumentId(documentId);
        //TODO:
        Page<FeedbackEntity> feedbackEntityPage = feedbackRepository.findAllByDocumentId(documentEntity.getId(), pageable);
       // Page<FeedbackEntity> feedbackEntityPage = feedbackRepository.findFeedbackEntitiesByDocument(documentEntity, pageable);


        List<FeedbackResponse> feedbackResponseList = feedbackEntityPage
                .stream()
                .map(FeedbackUtils::mapFeedbackEntityToFeedbackResponse)
                .collect(Collectors.toList());
        log.info("Returning {} feedbacks for document {}", feedbackResponseList.size(), documentId);
        return new PageImpl<>(
                feedbackResponseList,
                pageable,
                feedbackEntityPage.getTotalElements());
    }

    @Override
    public FeedbackResponse getFeedbackById(String feedbackId) {
        return mapFeedbackEntityToFeedbackResponse(findFeedbackById(feedbackId));
    }

    @Override
    public FeedbackResponse updateFeedback(String feedbackId, String userId, FeedbackRequest feedbackRequest) {
        var feedbackEntity = findFeedbackById(feedbackId);
        validateFeedbackOwnerPermission(feedbackEntity, userId);
        feedbackEntity.setComment(feedbackRequest.getComment());
        feedbackEntity.setDocumentRating(feedbackRequest.getDocumentRating());
        feedbackRepository.save(feedbackEntity);
        return mapFeedbackEntityToFeedbackResponse(feedbackEntity);
    }

    @Override
    public void deleteFeedback(String feedbackId, String userId) {
        var feedbackEntity = findFeedbackById(feedbackId);
        validateFeedbackOwnerPermission(feedbackEntity, userId);
        feedbackRepository.delete(feedbackEntity);
    }


    private FeedbackEntity buildFeedbackEntity(FeedbackRequest feedbackRequest,
                                               DocumentEntity documentEntity,
                                               UserEntity userEntity) {
        return FeedbackEntity.builder()
                .feedbackId(UUID.randomUUID().toString())
                .documentRating(feedbackRequest.getDocumentRating())
                .comment(feedbackRequest.getComment())
                .document(documentEntity)
                .owner(userEntity)
                .ownerFullName(userEntity.getFullName())
                .build();
    }


    //TODO:
    private Pageable createPageable(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return PageRequest.of(pageNo, pageSize, sort);
    }

    private FeedbackEntity findFeedbackById(String feedbackId) {
        return feedbackRepository.findByFeedbackId(feedbackId)
                .orElseThrow(() -> new NotFoundException("Feedback not found"));
    }

    private void validateFeedbackOwnerPermission(FeedbackEntity feedbackEntity, String userId) {
        if (!feedbackEntity.getOwner().getUserId().equals(userId)) {
            throw new ApiException("You do not have permission to delete or update this feedback");
        }
    }
}
