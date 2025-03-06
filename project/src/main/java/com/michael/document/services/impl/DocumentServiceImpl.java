package com.michael.document.services.impl;

import com.michael.document.domain.response.DocumentResponse;
import com.michael.document.entity.DocumentEntity;
import com.michael.document.entity.UserEntity;
import com.michael.document.exception.payload.ApiException;
import com.michael.document.repositories.DocumentRepository;
import com.michael.document.services.DocumentService;
import com.michael.document.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static com.michael.document.utils.DocumentUtils.*;
import static com.michael.document.utils.FileCompressor.decompressData;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static com.michael.document.utils.FileCompressor.compressData;
import static org.springframework.util.StringUtils.cleanPath;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DocumentServiceImpl implements DocumentService {

    public static final String NO_DOCUMENT_FOUND_BY_ID = "No document found by ID: %s";
    public static final String UNABLE_TO_SAVE_DOCUMENTS = "Unable to save documents";
    public static final String INVALID_FILE_NAME = "Invalid file name: %s";
    //  public static final String UNABLE_TO_UPDATE_DOCUMENTS = "Unable to update documents";
    final String DOCUMENT_RETRIEVAL_ERROR = "Unable to retrieve document with ID: %s";

    private final DocumentRepository documentRepository;
    private final UserService userService;


    @Override
    public Collection<DocumentResponse> saveDocument(String userId, List<MultipartFile> documents) {
        List<DocumentResponse> listDocuments = new ArrayList<>();
        var userEntity = userService.getUserEntityByUserId(userId);
        try {
            for (MultipartFile document : documents) {
                String filename = cleanPath(Objects.requireNonNull(document.getOriginalFilename()));
                if ("..".contains(filename)) {
                    throw new ApiException(String.format(INVALID_FILE_NAME, filename));
                }
                var savedDocument = buildDocumentEntity(filename, document, userEntity);
                log.info("Saved document in database by name: {}", savedDocument.getName());
                listDocuments.add(convertDocumentEntityToResponse(savedDocument));
            }
            return listDocuments;
        } catch (Exception exception) {
            throw new ApiException(UNABLE_TO_SAVE_DOCUMENTS);
        }
    }


    //TODO: увеличить возможности редактирования
    @Override
    public DocumentResponse updateDocument(String userId, String documentId, String name, String description) {
        DocumentEntity documentEntity = getDocumentEntity(documentId);
        validateDocumentOwnerPermission(documentEntity, userId);
        documentEntity.setName(name);
        documentEntity.setDescription(description);
        documentRepository.save(documentEntity);
        return convertDocumentEntityToResponse(documentEntity);
    }

    @Override
    public void deleteDocument(String userId, String documentId) {
        DocumentEntity documentEntity = getDocumentEntity(documentId);
        validateDocumentOwnerPermission(documentEntity, userId);
        documentRepository.delete(documentEntity);
        log.info("Document with id {} was deleted", documentId);
    }

    @Override
    public DocumentResponse getDocumentResponseByDocumentId(String documentId) {
        return convertDocumentEntityToResponse(getDocumentEntity(documentId));
    }

    @Override
    public DocumentEntity getDocumentEntityByDocumentId(String documentId) {
        return getDocumentEntity(documentId);
    }

    @Override
    public Resource getResource(String documentId) {
        var documentEntity = getDocumentEntity(documentId);
        try {
            ByteArrayResource byteArrayResource = new ByteArrayResource(decompressData(documentEntity.getData()));
            //  documentEntity.setDownloadCount(documentEntity.getDownloadCount() + 1);
            documentRepository.save(documentEntity);
            return byteArrayResource;
        } catch (Exception exception) {
            throw new ApiException(String.format(DOCUMENT_RETRIEVAL_ERROR, documentId));
        }
    }

    @Override
    public Page<DocumentResponse> getAllDocuments(int pageNo, int pageSize, String sortBy, String sortDir) {
        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        Page<DocumentEntity> documentEntities = documentRepository.findAll(pageable);
        return convertToDocumentResponsePage(documentEntities, pageable);
    }

    @Override
    public Page<DocumentResponse> searchAllDocumentsByNameOrDescription(String query, int pageNo,
                                                                        int pageSize, String sortBy,
                                                                        String sortDir) {
        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        Page<DocumentEntity> documentEntities = documentRepository.searchDocuments(query, pageable);
        return convertToDocumentResponsePage(documentEntities, pageable);
    }

    @Override
    public Page<DocumentResponse> getAllUserDocument(String userId,
                                                     int pageNo, int pageSize,
                                                     String sortBy, String sortDir) {
        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        UserEntity userEntity = userService.getUserEntityByUserId(userId);
        Page<DocumentEntity> documentEntities = documentRepository.findDocumentEntityByOwner(userEntity, pageable);
        return convertToDocumentResponsePage(documentEntities, pageable);
    }

    @Override
    public DocumentResponse toggleLike(String username, String documentId) {
        return null;
    }


    private Pageable createPageable(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return PageRequest.of(pageNo, pageSize, sort);
    }


    private DocumentEntity getDocumentEntity(String documentId) {
        return documentRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new ApiException(String.format(NO_DOCUMENT_FOUND_BY_ID, documentId)));
    }

    // Проверяем, является ли текущий пользователь владельцем документа
    private void validateDocumentOwnerPermission(DocumentEntity documentEntity, String userId) {
        if (!documentEntity.getOwner().getUserId().equals(userId)) {
            throw new ApiException("You do not have permission to delete or update this document");
        }
    }

    private DocumentEntity buildDocumentEntity(String filename, MultipartFile document, UserEntity userEntity) throws IOException {
        var documentEntity = DocumentEntity.builder()
                .documentId(UUID.randomUUID().toString())
                .name(filename)
                .data(compressData(document.getBytes()))
                .extension(getExtension(filename))
                .uri(getDocumentUri(filename))
                .formattedSize(byteCountToDisplaySize(document.getSize()))
                .icon(setIcon(getExtension(filename)))
                .owner(userEntity)
                //    .feedbacks(Collections.EMPTY_LIST)
                //    .totalLikes(0)
                //  .likedUsers(Collections.EMPTY_SET)
                //.downloadCount(0L)
                .build();
        return documentRepository.save(documentEntity);
    }
}
