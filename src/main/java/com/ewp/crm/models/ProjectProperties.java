package com.ewp.crm.models;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalTime;

//TODO Дополнить при необходимости полями для системных настроек
@Entity
@Table(name = "project_properties")
public class ProjectProperties {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id = 1L;

    @Column(name = "technical_account_token")
    private String technicalAccountToken;

    //ID статуса по умолчанию для клиентов (еще не студентов) вошедших в слак
    @Column(name = "default_status")
    private Long defaultStatusId = -1L;

    //ID статуса по умолчанию для повторно обративщихся клиентов
    @Column(name = "repeated_default_status")
    private Long repeatedDefaultStatusId = 1L;

    @Column(name = "new_client_status")
    private Long newClientStatus = 1L;

    // ID статуса по-умолчанию для клиентов студентов-отказников
    @Column(name = "client_reject_student_status")
    private Long clientRejectStudentStatus = -1L;

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

    /**
     * Auto-answer template for requests from java-mentor.com
     */
    @OneToOne
    @JoinColumn(name = "auto_answer_template")
    private MessageTemplate autoAnswerTemplate;

    @Column(name = "status_color")
    private String statusColor;

    /**
     * Цена месяца обучения по-умолчанию.
     */
    @Column(name = "default_price_per_month")
    private BigDecimal defaultPricePerMonth = new BigDecimal(12000.00);

    /**
     * Оплата по-умолчанию
     */
    @Column(name = "default_payment")
    private BigDecimal defaultPayment = new BigDecimal(12000.00);

    /**
     * Cтатус по-умолчанию для нового студента.
     */
    @OneToOne
    @JoinColumn(name = "default_student_status")
    private StudentStatus defaultStudentStatus;

    /**
     * Номер последнего договора об оплате
     */
    @Column(name = "contract_last_id")
    private Long contractLastId;

    /**
     * Шаблон отправки сообщения договора
     */
    @OneToOne
    @JoinColumn(name = "contract_template")
    private MessageTemplate contractTemplate;

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

    public Long getRepeatedDefaultStatusId() {
        return repeatedDefaultStatusId;
    }

    public void setRepeatedDefaultStatusId(Long repeatedDefaultStatusId) {
        this.repeatedDefaultStatusId = repeatedDefaultStatusId;
    }

    public Long getClientRejectStudentStatus() {
        return clientRejectStudentStatus;
    }

    public void setClientRejectStudentStatus(Long clientRejectStudentStatus) {
        this.clientRejectStudentStatus = clientRejectStudentStatus;
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

    public String getStatusColor() {
        return statusColor;
    }

    public void setStatusColor(String statusColor) {
        this.statusColor = statusColor;
    }

    public Long getNewClientStatus() {
        return newClientStatus;
    }

    public void setNewClientStatus(Long newClientStatus) {
        this.newClientStatus = newClientStatus;
    }

    public BigDecimal getDefaultPricePerMonth() {
        return defaultPricePerMonth;
    }

    public void setDefaultPricePerMonth(BigDecimal defaultPricePerMonth) {
        this.defaultPricePerMonth = defaultPricePerMonth;
    }

    public BigDecimal getDefaultPayment() {
        return defaultPayment;
    }

    public void setDefaultPayment(BigDecimal defaultPayment) {
        this.defaultPayment = defaultPayment;
    }

    public StudentStatus getDefaultStudentStatus() {
        return defaultStudentStatus;
    }

    public void setDefaultStudentStatus(StudentStatus defaultStudentStatus) {
        this.defaultStudentStatus = defaultStudentStatus;
    }

    public MessageTemplate getAutoAnswerTemplate() {
        return autoAnswerTemplate;
    }

    public Long getContractLastId() {
        return contractLastId;
    }

    public void setContractLastId(Long contractLastId) {
        this.contractLastId = contractLastId;
    }

    public MessageTemplate getContractTemplate() {
        return contractTemplate;
    }

    public void setContractTemplate(MessageTemplate contractTemplate) {
        this.contractTemplate = contractTemplate;
    }

    public void setAutoAnswerTemplate(MessageTemplate autoAnswerTemplate) {
        this.autoAnswerTemplate = autoAnswerTemplate;
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
