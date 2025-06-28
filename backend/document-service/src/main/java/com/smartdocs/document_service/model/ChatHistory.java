package com.smartdocs.document_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "chat_history")
public class ChatHistory {

    @Id
    private String id;

    private String userId;
    private String question;
    private String response;
    private LocalDateTime timestamp;

    public ChatHistory() {}

    public ChatHistory(String userId, String question, String response, LocalDateTime timestamp) {
        this.userId = userId;
        this.question = question;
        this.response = response;
        this.timestamp = timestamp;
    }

    // Getters and setters

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
