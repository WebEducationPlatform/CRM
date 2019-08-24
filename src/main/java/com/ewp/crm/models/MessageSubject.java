package com.ewp.crm.models;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "message_subject")
public class MessageSubject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private Long subject_id;

    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "id")
    private MessageTemplate messageTemplate;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "status_id")
    private Status status;

    public MessageSubject() {
    }

    public MessageSubject(String title, MessageTemplate messageTemplate, Status status) {
        this.title = title;
        this.messageTemplate = messageTemplate;
        this.status = status;
    }

    public Long getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(Long subject_id) {
        this.subject_id = subject_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MessageTemplate getMessageTemplate() {
        return messageTemplate;
    }

    public void setMessageTemplate(MessageTemplate messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
