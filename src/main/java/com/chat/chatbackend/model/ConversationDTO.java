package com.chat.chatbackend.model;

public class ConversationDTO {
    private String username;
    private String lastMessage;
    private String timestamp;

    public ConversationDTO(String username, String lastMessage, String timestamp) {
        this.username = username;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
