package com.ewp.crm.models;

import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.Diffable;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Студент
 */
@Table (name = "student")
@EntityListeners(StudentListener.class)
@Entity
public class Student implements Diffable<Student> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "id")
    private Long id;

    /**
     * Клиент, который стал студентом (изначально клиент студентом не является)
     */
    @JoinColumn (name = "client_id")
    @OneToOne
    private Client client;

    /**
     * Дата окончания пробного периода обучения
     */
    @Column (name = "end_trial")
    private LocalDateTime trialEndDate;

    /**
     * Дата следующего платежа
     */
    @Column (name = "next_pay")
    private LocalDateTime nextPaymentDate;

    /**
     * Дата последнего уведомления об оплате
     */
    @Column (name = "last_payment_notification")
    private LocalDateTime lastPaymentNotification;

    /**
     * Стоимость обучения в месяц, руб
     */
    @Column (name = "price")
    private BigDecimal price;

    /**
     * Уже получено от студента, руб
     */
    @Column (name = "amount")
    private BigDecimal paymentAmount;

    /**
     * Осталось получить со студента (долг): price - paymentAmount
     */
    @Column (name = "later")
    private BigDecimal payLater;

    /**
     * Специальность (направление обучения) студента (Java Core, Java Web и тд) (Статус???)
     */
    @JoinColumn (name = "status_id")
    @OneToOne
    private StudentStatus status;

    /**
     * Заметки по студенту
     */
    @Column (name = "notes")
    private String notes;

    /**
     * Напоминать ли студенту по электронке (поля ниже по смс, в vk) об оплате
     */
    @Column (name = "notify_email")
    private boolean notifyEmail = false;

    @Column (name = "notify_sms")
    private boolean notifySMS = false;

    @Column (name = "notify_vk")
    private boolean notifyVK = false;

    @Column (name = "notify_slack")
    private boolean notifySlack = false;

    @Column (name = "color")
    private String color;

    public Student() {
    }

    public Student(Client client, LocalDateTime trialEndDate, LocalDateTime nextPaymentDate, BigDecimal price, BigDecimal paymentAmount, BigDecimal payLater, StudentStatus status, String notes) {
        this.client = client;
        this.trialEndDate = trialEndDate;
        this.nextPaymentDate = nextPaymentDate;
        this.price = price;
        this.paymentAmount = paymentAmount;
        this.payLater = payLater;
        this.status = status;
        this.notes = notes;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public LocalDateTime getTrialEndDate() {
        return trialEndDate;
    }

    public void setTrialEndDate(LocalDateTime trialEndDate) {
        this.trialEndDate = trialEndDate;
    }

    public LocalDateTime getNextPaymentDate() {
        return nextPaymentDate;
    }

    public void setNextPaymentDate(LocalDateTime nextPaymentDate) {
        this.nextPaymentDate = nextPaymentDate;
    }

    public LocalDateTime getLastPaymentNotification() {
        return lastPaymentNotification;
    }

    public void setLastPaymentNotification(LocalDateTime lastPaymentNotification) {
        this.lastPaymentNotification = lastPaymentNotification;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public BigDecimal getPayLater() {
        return payLater;
    }

    public void setPayLater(BigDecimal payLater) {
        this.payLater = payLater;
    }

    public StudentStatus getStatus() {
        return status;
    }

    public void setStatus(StudentStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isNotifyEmail() {
        return notifyEmail;
    }

    public void setNotifyEmail(boolean notifyEmail) {
        this.notifyEmail = notifyEmail;
    }

    public boolean isNotifySMS() {
        return notifySMS;
    }

    public void setNotifySMS(boolean notifySMS) {
        this.notifySMS = notifySMS;
    }

    public boolean isNotifyVK() {
        return notifyVK;
    }

    public void setNotifyVK(boolean notifyVK) {
        this.notifyVK = notifyVK;
    }

    public boolean isNotifySlack() {
        return notifySlack;
    }

    public void setNotifySlack(boolean notifySlack) {
        this.notifySlack = notifySlack;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", client=" + client +
                ", trialEndDate=" + trialEndDate +
                ", nextPaymentDate=" + nextPaymentDate +
                ", lastPaymentNotification=" + lastPaymentNotification +
                ", price=" + price +
                ", paymentAmount=" + paymentAmount +
                ", payLater=" + payLater +
                ", status=" + status +
                ", notes='" + notes +
                ", notifyEmail='" + notifyEmail +
                ", notifySMS='" + notifySMS +
                ", notifyVK='" + notifyVK +
                '}';
    }

    @Override
    public DiffResult diff(Student student) {
        return new DiffBuilder(this, student, ToStringStyle.JSON_STYLE)
                .append("Клиент", this.client.getId(), student.client.getId())
                .append("Дата пробных", this.trialEndDate.toLocalDate(), student.trialEndDate.toLocalDate())
                .append("Дата оплаты", this.nextPaymentDate.toLocalDate(), student.nextPaymentDate.toLocalDate())
                .append("Цена", this.price.toString().contains(".00") ? this.price.toBigInteger().toString() : this.price.toString(), student.price.toString().contains(".00") ? student.price.toBigInteger().toString() : student.price.toString())
                .append("Платёж", this.paymentAmount.toString().contains(".00") ? this.paymentAmount.toBigInteger().toString() : this.paymentAmount.toString(), student.paymentAmount.toString().contains(".00") ? student.paymentAmount.toBigInteger().toString() : student.paymentAmount.toString())
                .append("Оплата позже", this.payLater.toString().contains(".00") ? this.payLater.toBigInteger().toString() : this.payLater.toString(), student.payLater.toString().contains(".00") ? student.payLater.toBigInteger().toString() : student.payLater.toString())
                .append("Статус обучения", this.status.getStatus(), student.status.getStatus())
                .append("Заметки", this.notes, student.notes)
                .append("Оповещение по почте", this.notifyEmail, student.notifyEmail)
                .append("Оповещение по СМС", this.notifySMS, student.notifySMS)
                .append("Оповещение по Вконтакте", this.notifyVK, student.notifyVK)
                .build();
    }
}
