package com.smartdocs.document_service.repository;
import com.smartdocs.document_service.model.DocumentChunk;
import com.smartdocs.document_service.model.ChatHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatHistoryRepository extends MongoRepository<ChatHistory, String> {
    List<ChatHistory> findByUserIdOrderByTimestampDesc(String userId);
    List<ChatHistory> findByUserId(String userId);

}
