package com.ewp.crm.models.dto;

import org.springframework.stereotype.Component;

@Component
public class MailDto {

    private long userId;

    private String content;

    private String sentDate;

    private String subject;

    private String sentFrom;

    public MailDto() {}

    public MailDto(long userId, String content, String sentDate, String subject, String sentFrom) {
        this.userId = userId;
        this.content = content;
        this.sentDate = sentDate;
        this.subject = subject;
        this.sentFrom = sentFrom;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSentDate() {
        return sentDate;
    }

    public void setSentDate(String sentDate) {
        this.sentDate = sentDate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getSentFrom() {
        return sentFrom;
    }

    public void setSentFrom(String sentFrom) {
        this.sentFrom = sentFrom;
    }

    @Override
    public String toString() {
        return "MailDto{" +
                "userId=" + userId +
                ", content='" + content + '\'' +
                ", sentDate='" + sentDate + '\'' +
                ", subject='" + subject + '\'' +
                ", sentFrom='" + sentFrom + '\'' +
                '}';
    }
}
