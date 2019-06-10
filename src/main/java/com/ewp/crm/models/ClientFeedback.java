package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "feedback")
public class ClientFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long id;

    @Column(name = "social_url")
    private String socialUrl;

    @Lob
    @Column(name = "text")
    private String text;

    @Column(name = "video_url")
    private String videoUrl;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "feedback_client",
            joinColumns = {@JoinColumn(name = "feedback_id", foreignKey = @ForeignKey(name = "FK_FEEDBACK"))},
            inverseJoinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_CLIENT"))})
    private Client client;

    public ClientFeedback() {}

    public ClientFeedback(String socialUrl, String text, String videoUrl) {
        this.socialUrl = socialUrl;
        this.text = text;
        this.videoUrl = videoUrl;
    }

    public Long getId() {
        return id;
    }

    public String getSocialUrl() {
        return socialUrl;
    }

    public void setSocialUrl(String socialUrl) {
        this.socialUrl = socialUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientFeedback that = (ClientFeedback) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, socialUrl, text, videoUrl);
    }
}
