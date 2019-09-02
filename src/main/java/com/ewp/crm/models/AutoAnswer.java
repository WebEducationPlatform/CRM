package com.ewp.crm.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "answers")
public class AutoAnswer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "subject", unique = true)
    private String subject;

    @OneToOne
    @JoinColumn(name = "messagetemplate_id")
    private MessageTemplate messageTemplate;

    @OneToOne
    @JoinColumn(name = "status_id")
    private Status status;

    public AutoAnswer() {
    }

    public AutoAnswer(String subject) {
        this.subject = subject;
    }

    public AutoAnswer(String subject, MessageTemplate messageTemplate, Status status) {
        this.subject = subject;
        this.messageTemplate = messageTemplate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AutoAnswer that = (AutoAnswer) o;
        return subject.equals(that.subject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject);
    }
}
