package com.ewp.crm.models;

import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.Diffable;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Type;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table (name = "student")
@Entity
public class Student implements Diffable<Student> {

    @Id
    @GeneratedValue
    @Column (name = "id")
    private Long id;

    @JoinColumn (name = "client_id")
    @OneToOne
    private Client client;

    @Column (name = "end_trial")
    private LocalDateTime trialEndDate;

    @Column (name = "next_pay")
    private LocalDateTime nextPaymentDate;

    @Column (name = "price")
    private BigDecimal price;

    @Column (name = "amount")
    private BigDecimal paymentAmount;

    @Column (name = "later")
    private BigDecimal payLater;

    @JoinColumn (name = "status_id")
    @OneToOne
    private StudentStatus status;

    @Column (name = "notes")
    private String notes;

    @Column (name = "notify_email")
    private boolean notifyEmail = false;

    @Column (name = "notify_sms")
    private boolean notifySMS = false;

    @Column (name = "notify_vk")
    private boolean notifyVK = false;

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

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", client=" + client +
                ", trialEndDate=" + trialEndDate +
                ", nextPaymentDate=" + nextPaymentDate +
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
        return new DiffBuilder(this, client, ToStringStyle.JSON_STYLE)
                .append("Дата пробных", this.trialEndDate, student.trialEndDate)
                .append("Двта оплаты", this.nextPaymentDate, student.nextPaymentDate)
                .append("Цена", this.price, student.price)
                .append("Платёж", this.paymentAmount, student.paymentAmount)
                .append("Оплата позже", this.payLater, student.payLater)
                .append("Статус обучения", this.status, student.status)
                .append("Заметки", this.notes, student.notes)
                .append("Оповещение по почте", this.notifyEmail, student.notifyEmail)
                .append("Оповещение по СМС", this.notifySMS, student.notifySMS)
                .append("Оповещение по Вконтакте", this.notifyVK, student.notifyVK)
                .build();
    }
}
