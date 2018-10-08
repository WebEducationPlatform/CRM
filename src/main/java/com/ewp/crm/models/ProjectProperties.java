package com.ewp.crm.models;


import javax.persistence.*;
import java.time.LocalTime;

//TODO Дополнить при необходимости полями для системных настроек
@Entity
@Table(name = "project_properties")
public class ProjectProperties {

    @Id
    @Column(name = "id")
    private Long id = 1L;

    @Column(name = "technical_account_token")
    private String technicalAccountToken;

    //ID статуса по умолчанию для клиентов (еще не студентов) вошедших в слак
    @Column(name = "default_status")
    private Long defaultStatusId;

    /**
     * Message template for scheduled payment notification.
     */
    @OneToOne
    @JoinColumn(name = "payment_message_template")
    private MessageTemplate paymentMessageTemplate;

    /**
     * Time of the day payment notification invoked in.
     */
    @Column(name = "payment_notification_time")
    private LocalTime paymentNotificationTime;

    /**
     * Is payment notification enabled.
     */
    @Column(name = "payment_notification_enabled")
    private boolean paymentNotificationEnabled = false;

    public ProjectProperties() {
    }

    public ProjectProperties(String technicalAccountToken) {
        this.technicalAccountToken = technicalAccountToken;
    }

    public ProjectProperties(Long id, String technicalAccountToken) {
        this.id = id;
        this.technicalAccountToken = technicalAccountToken;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTechnicalAccountToken() {
        return technicalAccountToken;
    }

    public void setTechnicalAccountToken(String technicalAccountToken) {
        this.technicalAccountToken = technicalAccountToken;
    }

    public Long getDefaultStatusId() {
        return defaultStatusId;
    }

    public void setDefaultStatusId(Long defaultStatusId) {
        this.defaultStatusId = defaultStatusId;
    }

    public MessageTemplate getPaymentMessageTemplate() {
        return paymentMessageTemplate;
    }

    public void setPaymentMessageTemplate(MessageTemplate paymentMessageTemplate) {
        this.paymentMessageTemplate = paymentMessageTemplate;
    }

    public LocalTime getPaymentNotificationTime() {
        return paymentNotificationTime;
    }

    public void setPaymentNotificationTime(LocalTime paymentNotificationTime) {
        this.paymentNotificationTime = paymentNotificationTime;
    }

    public boolean isPaymentNotificationEnabled() {
        return paymentNotificationEnabled;
    }

    public void setPaymentNotificationEnabled(boolean paymentNotificationEnabled) {
        this.paymentNotificationEnabled = paymentNotificationEnabled;
    }

    @Override
    public String toString() {
        return "ProjectProperties{" +
                "id=" + id +
                ", technicalAccountToken='" + technicalAccountToken + '\'' +
                ", defaultStatusId=" + defaultStatusId +
                ", paymentMessageTemplate=" + paymentMessageTemplate +
                ", paymentNotificationTime=" + paymentNotificationTime +
                ", paymentNotificationEnabled=" + paymentNotificationEnabled +
                '}';
    }
}
