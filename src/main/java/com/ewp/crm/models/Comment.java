package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.Objects;

import javax.persistence.*;

@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_USER"))
    private User user;

    @JsonIgnore
    @ManyToOne(targetEntity = Client.class)
    @JoinTable(name = "client_comment",
            joinColumns = {@JoinColumn(name = "comment_id", foreignKey = @ForeignKey(name = "FK_CLIENT"))},
            inverseJoinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_COMMENT"))})
    private Client client;

    @Column(name = "date")
    private Date date = new Date();

    @Column(name = "content")
    private String content;

    public Comment() {
    }

    public Comment(User user, Client client, Date date, String content) {
        this.user = user;
        this.client = client;
        this.date = date;
        this.content = content;
    }

    public Comment(User user, Client client, String content) {
        this.user = user;
        this.client = client;
        this.content = content;
        this.date = new Date(System.currentTimeMillis());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comment comment = (Comment) o;

        if (id != null ? !id.equals(comment.id) : comment.id != null) return false;
        if (user != null ? !user.equals(comment.user) : comment.user != null) return false;
        if (client != null ? !client.equals(comment.client) : comment.client != null) return false;
        if (date != null ? !date.equals(comment.date) : comment.date != null) return false;
        return content != null ? content.equals(comment.content) : comment.content == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (client != null ? client.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }
}
