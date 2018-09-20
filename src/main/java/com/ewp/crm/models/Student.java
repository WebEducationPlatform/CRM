package com.ewp.crm.models;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import javax.persistence.*;
import java.math.BigDecimal;

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
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime trialEndDate;

    @Column (name = "next_pay")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime nextPaymentDate;

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

    public Student() {
    }

    public Student(Client client, DateTime trialEndDate, DateTime nextPaymentDate, BigDecimal price, BigDecimal paymentAmount, BigDecimal payLater, StudentStatus status, String notes) {
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

    public DateTime getTrialEndDate() {
        return trialEndDate;
    }

    public void setTrialEndDate(DateTime trialEndDate) {
        this.trialEndDate = trialEndDate;
    }

    public DateTime getNextPaymentDate() {
        return nextPaymentDate;
    }

    public void setNextPaymentDate(DateTime nextPaymentDate) {
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
