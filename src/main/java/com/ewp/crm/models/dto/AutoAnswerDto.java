package com.ewp.crm.models.dto;

import com.ewp.crm.models.AutoAnswer;

public class AutoAnswerDto {
    private Long id;
    private String subject;
    private Long messageTemplate_id;
    private Long status_id;

    public AutoAnswerDto() {
    }

    public AutoAnswerDto(AutoAnswer autoAnswer) {
        this.id = autoAnswer.getId();
        this.subject = autoAnswer.getSubject();
        this.messageTemplate_id = autoAnswer.getMessageTemplate() != null ? autoAnswer.getMessageTemplate().getId() : -1L;
        this.status_id =  autoAnswer.getStatus() != null ? autoAnswer.getStatus().getId() : -1L;
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

    public Long getMessageTemplate_id() {
        return messageTemplate_id;
    }

    public void setMessageTemplate_id(Long messageTemplate_id) {
        this.messageTemplate_id = messageTemplate_id;
    }

    public Long getStatus_id() {
        return status_id;
    }

    public void setStatus_id(Long status_id) {
        this.status_id = status_id;
    }
}
