package com.smartdocs.document_service.dto;

public class ChatHistoryRequest {
   

    private String question;
    private String response;

    // Default constructor (important for Jackson)
    public ChatHistoryRequest() {}

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
}
