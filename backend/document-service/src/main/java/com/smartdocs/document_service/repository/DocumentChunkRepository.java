package com.smartdocs.document_service.repository;

import com.smartdocs.document_service.model.DocumentChunk;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DocumentChunkRepository extends MongoRepository<DocumentChunk, String> {
    List<DocumentChunk> findByDocumentId(String documentId);
    List<DocumentChunk> findByUserId(String userId);
}
