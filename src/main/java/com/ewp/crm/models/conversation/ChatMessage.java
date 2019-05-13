package com.ewp.crm.models.conversation;

import java.time.ZonedDateTime;

public class ChatMessage {

    private String id;
    private String chatId;
    private ChatType chatType;
    private String text;
    private ZonedDateTime time;
    private Boolean isRead;
    private Boolean isOutgoing;

    public ChatMessage(String id, String chatId, ChatType chatType, String text, ZonedDateTime time, Boolean isRead, Boolean isOutgoing) {
        this.id = id;
        this.chatId = chatId;
        this.chatType = chatType;
        this.text = text;
        this.time = time;
        this.isRead = isRead;
        this.isOutgoing = isOutgoing;
    }
    public ChatMessage(String chatId, ChatType chatType, String text, ZonedDateTime time, Boolean isRead, Boolean isOutgoing) {
        this.chatId = chatId;
        this.chatType = chatType;
        this.text = text;
        this.time = time;
        this.isRead = isRead;
        this.isOutgoing = isOutgoing;
    }

    public ChatMessage(ChatType chatType, String text) {
        this.chatType = chatType;
        this.text = text;
    }

    public ChatMessage(String text, ChatType chatType, String chatId) {
        this.chatType = chatType;
        this.text = text;
        this.chatId = chatId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public ZonedDateTime getTime() {
        return time;
    }

    public void setTime(ZonedDateTime time) {
        this.time = time;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public void setOutgoing(Boolean outgoing) {
        isOutgoing = outgoing;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public Boolean getOutgoing() {
        return isOutgoing;
    }
}
