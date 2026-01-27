package com.example.reseau_social.websocket;

public class WebSocketMessageDto {
    private String action; // JOIN_CONVERSATION, SEND_MESSAGE, LEAVE_CONVERSATION
    private String conversationId;
    private Integer userId;
    private String content;

    public WebSocketMessageDto() {}

    public WebSocketMessageDto(String action, String conversationId, Integer userId, String content) {
        this.action = action;
        this.conversationId = conversationId;
        this.userId = userId;
        this.content = content;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
