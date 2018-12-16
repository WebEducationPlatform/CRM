package com.ewp.crm.service.conversation;

import java.time.LocalDateTime;

public class ChatMessage {

    private Long id;
    private ChatType chatType;
    private String text;
    private LocalDateTime time;
    private Boolean isRead;
    private Boolean isOutgoing;


    public ChatMessage(ChatType chatType, String text) {
        this.chatType = chatType;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ChatType getChatType() {
        return chatType;
    }

    public void setChatType(ChatType chatType) {
        this.chatType = chatType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public Boolean isOutgoing() {
        return isOutgoing;
    }

    public void setOutgoing(Boolean outgoing) {
        isOutgoing = outgoing;
    }
}
