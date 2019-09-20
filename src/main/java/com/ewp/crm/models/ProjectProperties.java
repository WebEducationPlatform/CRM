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

    //ID статуса по умолчанию для повторно обративщихся клиентов
    @Column(name = "repeated_default_status")
    private Long repeatedDefaultStatusId = 1L;

    @Column(name = "new_client_status")
    private Long newClientStatus = 1L;

    // ID статуса по-умолчанию для клиентов студентов-отказников
    @Column(name = "client_reject_student_status")
    private Long clientRejectStudentStatus = -1L;

    // ID статуса по-умолчанию для впервые оплативших клиентов
    @Column(name = "client_first_pay_status")
    private Long clientFirstPayStatus = -1L;

    // ID статуса по-умолчанию для клиентов после первого созвона
    @Column(name = "first_skype_call_after_status")
    private Long firstSkypeCallAfterStatus = -1L;

    /**
     * Message template for scheduled payment notification.
     */
    @OneToOne
    @JoinColumn(name = "payment_message_template")
    private MessageTemplate paymentMessageTemplate;

    /**
     * Message template for scheduled trial notification.
     */
    @OneToOne
    @JoinColumn(name = "trial_message_template")
    private MessageTemplate trialMessageTemplate;

    /**
     * Message template for new Client notification.
     */
    @OneToOne
    @JoinColumn(name = "new_client_message_template")
    private MessageTemplate newClientMessageTemplate;

    /**
     * Message template for birth day notification.
     */
    @OneToOne
    @JoinColumn(name = "birth_day_message_template")
    private MessageTemplate birthDayMessageTemplate;

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
     * Time of the day trial notification invoked in.
     */
    @Column(name = "trial_notification_time")
    private LocalTime trialNotificationTime;

    /**
     * Is trial notification enabled.
     */
    @Column(name = "trial_notification_enabled")
    private boolean trialNotificationEnabled = false;

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

    @Column(name = "slack_default_users")
    private String slackDefaultUsers;

    @Column(name = "slack_invite_link")
    private String slackInviteLink;

    /**
     * Банковские реквизиты
     */
    @Column(name = "inn")
    private String inn;

    @Column(name = "bank_checking_account")
    private String checkingAccount;

    @Column(name = "bank_correspondent_account")
    private String correspondentAccount;

    @Column(name = "bank_identification_code")
    private String bankIdentificationCode;

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

    public MessageTemplate getTrialMessageTemplate() {
        return trialMessageTemplate;
    }

    public void setTrialMessageTemplate(MessageTemplate trialMessageTemplate) {
        this.trialMessageTemplate = trialMessageTemplate;
    }

    public LocalTime getTrialNotificationTime() {
        return trialNotificationTime;
    }

    public void setTrialNotificationTime(LocalTime trialNotificationTime) {
        this.trialNotificationTime = trialNotificationTime;
    }

    public boolean isTrialNotificationEnabled() {
        return trialNotificationEnabled;
    }

    public void setTrialNotificationEnabled(boolean trialNotificationEnabled) {
        this.trialNotificationEnabled = trialNotificationEnabled;
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

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getCheckingAccount() {
        return checkingAccount;
    }

    public void setCheckingAccount(String checkingAccount) {
        this.checkingAccount = checkingAccount;
    }

    public String getCorrespondentAccount() {
        return correspondentAccount;
    }

    public void setCorrespondentAccount(String correspondentAccount) {
        this.correspondentAccount = correspondentAccount;
    }

    public String getBankIdentificationCode() {
        return bankIdentificationCode;
    }

    public void setBankIdentificationCode(String bankIdentificationCode) {
        this.bankIdentificationCode = bankIdentificationCode;
    }

    public MessageTemplate getNewClientMessageTemplate() {
        return newClientMessageTemplate;
    }

    public void setNewClientMessageTemplate(MessageTemplate newClientMessageTemplate) {
        this.newClientMessageTemplate = newClientMessageTemplate;
    }

    public Long getClientFirstPayStatus() {
        return clientFirstPayStatus;
    }

    public void setClientFirstPayStatus(Long clientFirstPayStatus) {
        this.clientFirstPayStatus = clientFirstPayStatus;
    }

    public String getSlackDefaultUsers() {
        return slackDefaultUsers;
    }

    public void setSlackDefaultUsers(String slackDefaultUsers) {
        this.slackDefaultUsers = slackDefaultUsers;
    }

    public String getSlackInviteLink() {
        return slackInviteLink;
    }

    public void setSlackInviteLink(String slackInviteLink) {
        this.slackInviteLink = slackInviteLink;
    }

    public MessageTemplate getBirthDayMessageTemplate() {
        return birthDayMessageTemplate;
    }

    public void setBirthDayMessageTemplate(MessageTemplate birthDayMessageTemplate) {
        this.birthDayMessageTemplate = birthDayMessageTemplate;
    }

    public Long getFirstSkypeCallAfterStatus() {
        return firstSkypeCallAfterStatus;
    }

    public void setFirstSkypeCallAfterStatus(Long firstSkypeCallAfterStatus) {
        this.firstSkypeCallAfterStatus = firstSkypeCallAfterStatus;
    }

    @Override
    public String toString() {
        return "ProjectProperties{" +
                "id=" + id +
                ", technicalAccountToken='" + technicalAccountToken + '\'' +
                ", paymentMessageTemplate=" + paymentMessageTemplate +
                ", paymentNotificationTime=" + paymentNotificationTime +
                ", paymentNotificationEnabled=" + paymentNotificationEnabled +
                '}';
    }
}
