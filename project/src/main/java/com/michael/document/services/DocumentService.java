package com.michael.document.services;

import com.michael.document.domain.response.DocumentResponse;
import com.michael.document.entity.DocumentEntity;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import java.util.Collection;
import java.util.List;

public interface DocumentService {
    Collection<DocumentResponse> saveDocument(String userId, List<MultipartFile> documents);

    DocumentResponse updateDocument(String userId,String documentId, String name, String description);

    void deleteDocument(String userId, String documentId);

    DocumentResponse getDocumentResponseByDocumentId(String documentId);

    DocumentEntity getDocumentEntityByDocumentId(String documentId);

    Resource getResource(String documentId);

    Page<DocumentResponse> getAllDocuments(int pageNo, int pageSize, String sortBy, String sortDir);

    Page<DocumentResponse> searchAllDocumentsByNameOrDescription(String query, int pageNo, int pageSize, String sortBy, String sortDir);

    Page<DocumentResponse>  getAllUserDocument(String userId, int pageNo, int pageSize, String sortBy, String sortDir);

    DocumentResponse toggleLike(String username, String documentId);
}
