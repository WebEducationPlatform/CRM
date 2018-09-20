package com.ewp.crm.models;

import javax.persistence.*;
import java.util.Date;

@Table (name = "student")
@Entity
public class Student {

    @Id
    @GeneratedValue
    @Column (name = "id")
    private Long id;

    @JoinColumn (name = "client_id")
    @OneToOne
    private Client client;

    @Column (name = "end_trial")
    private Date trialEndDate;

    @Column (name = "next_pay")
    private Date nextPaymentDate;

    @Column (name = "price")
    private Long price;

    @Column (name = "amount")
    private Long paymentAmount;

    @Column (name = "later")
    private Long payLater;

    @JoinColumn (name = "status_id")
    @OneToOne
    private StudentStatus status;

    @Column (name = "notes")
    private String notes;

    public Student() {
    }

    public Student(Client client, Date trialEndDate, Date nextPaymentDate, Long price, Long paymentAmount, Long payLater, StudentStatus status, String notes) {
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

    public Date getTrialEndDate() {
        return trialEndDate;
    }

    public void setTrialEndDate(Date trialEndDate) {
        this.trialEndDate = trialEndDate;
    }

    public Date getNextPaymentDate() {
        return nextPaymentDate;
    }

    public void setNextPaymentDate(Date nextPaymentDate) {
        this.nextPaymentDate = nextPaymentDate;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(Long paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public Long getPayLater() {
        return payLater;
    }

    public void setPayLater(Long payLater) {
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
                ", notes='" + notes + '\'' +
                '}';
    }
}
