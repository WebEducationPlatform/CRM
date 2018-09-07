package com.ewp.crm.models;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "youtube_client")
public class YoutubeClient {

    @Id
    @GeneratedValue
    @Column(name = "youtube_client_id")
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "youtube_client_youtube_client_message",
            joinColumns = {@JoinColumn(name = "youtube_client_id", foreignKey = @ForeignKey(name = "FK_YOUTUBE_CLIENT_YOUTUBE_CLIENT_MESSAGES"))},
            inverseJoinColumns = {@JoinColumn(name = "youtube_client_messages_id", foreignKey = @ForeignKey(name = "FK_YOUTUBE_CLIENT_MESSAGES"))})
    private List<YoutubeClientMessage> messages;

    public YoutubeClient() {
    }

    public YoutubeClient(String fullName, List<YoutubeClientMessage> messages) {
        this.fullName = fullName;
        this.messages = messages;
    }

    public YoutubeClient(String fullName) {
        this.fullName = fullName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<YoutubeClientMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<YoutubeClientMessage> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "YoutubeClient{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
