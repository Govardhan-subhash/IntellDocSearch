package com.smartdocs.document_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "document_chunks")
public class DocumentChunk {

    @Id
    private String id;

    private String documentId;   // ID of the document this chunk belongs to
    private String userId;       // ID of the user who owns this document
    private int chunkIndex;      // Index of the chunk in the document
    private String content;      // Content of the chunk

    private String fileName;     // e.g., "report.pdf"
    private String fileType; 
    private String fileHash;  // SHA-256 hash of the entire file content

// add getters and setters
public String getFileHash() {
    return fileHash;
}

public void setFileHash(String fileHash) {
    this.fileHash = fileHash;
}
    // e.g., "application/pdf"

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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
