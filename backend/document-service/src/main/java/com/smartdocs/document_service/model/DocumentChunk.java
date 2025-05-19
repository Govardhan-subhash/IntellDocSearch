package com.smartdocs.document_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "document_chunks")
public class DocumentChunk {

    @Id
    private String id;
    private String documentId; // ID of the document this chunk belongs to
    private String userId; // ID of the user who owns this document
    private int chunkIndex; // Index of the chunk in the document
    private String content; // Content of the chunk

    // Getters and Setters
    public String getId() {







        
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getChunkIndex() {
        return chunkIndex;
    }

    public void setChunkIndex(int chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
