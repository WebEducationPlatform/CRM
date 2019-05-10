package com.ewp.crm.models.conversation;

public class Interlocutor {

    private String id;
    private String profileUrl;
    private String avatarUrl;
    private String representation;
    private ChatType chatType;

    public Interlocutor(String id, String profileUrl, String avatarUrl, String representation, ChatType chatType) {
        this.id = id;
        this.profileUrl = profileUrl;
        this.avatarUrl = avatarUrl;
        this.representation = representation;
        this.chatType = chatType;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getRepresentation() {
        return representation;
    }

    public void setRepresentation(String representation) {
        this.representation = representation;
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
}
