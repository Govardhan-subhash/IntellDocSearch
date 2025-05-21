package com.smartdocs.document_service.repository;

import com.smartdocs.document_service.model.DocumentChunk;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface DocumentChunkRepository extends MongoRepository<DocumentChunk, String> {

    List<DocumentChunk> findByUserId(String userId);

    List<DocumentChunk> findByUserIdAndFileName(String userId, String fileName);

    // Return distinct file names as Strings for a given userId
    @Query(value = "{ 'userId': ?0 }", fields = "{ 'fileName' : 1 }")
    List<String> findDistinctFileNamesByUserId(String userId);

    // Add delete method by userId and fileName
    void deleteByUserIdAndFileName(String userId, String fileName);
    long countByUserIdAndFileName(String userId, String fileName);
}
