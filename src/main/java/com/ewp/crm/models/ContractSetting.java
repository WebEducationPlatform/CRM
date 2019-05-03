package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "contract_setting")
public class ContractSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hash;

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "one_time_payment")
    private boolean oneTimePayment;

    @Column(name = "month_payment")
    private boolean monthPayment;

    private boolean diploma;

    private boolean stamp;

    @Column(name = "payment_amount")
    private String paymentAmount;

    @JsonIgnore
    @JoinColumn (name = "user_id")
    @OneToOne
    private User user;

    public ContractSetting() {
    }

    public ContractSetting(String hash, Long clientId, boolean oneTimePayment, boolean monthPayment, boolean diploma, String paymentAmount) {
        this.hash = hash;
        this.clientId = clientId;
        this.oneTimePayment = oneTimePayment;
        this.monthPayment = monthPayment;
        this.diploma = diploma;
        this.paymentAmount = paymentAmount;
    }

    public Long getId() {
        return id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public boolean isOneTimePayment() {
        return oneTimePayment;
    }

    public void setOneTimePayment(boolean oneTimePayment) {
        this.oneTimePayment = oneTimePayment;
    }

    public boolean isDiploma() {
        return diploma;
    }

    public void setDiploma(boolean diploma) {
        this.diploma = diploma;
    }

    public String getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(String paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public boolean isMonthPayment() {
        return monthPayment;
    }

    public void setMonthPayment(boolean monthPayment) {
        this.monthPayment = monthPayment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isStamp() {
        return stamp;
    }

    public void setStamp(boolean stamp) {
        this.stamp = stamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContractSetting that = (ContractSetting) o;
        return oneTimePayment == that.oneTimePayment &&
                diploma == that.diploma &&
                Objects.equals(hash, that.hash) &&
                Objects.equals(clientId, that.clientId) &&
                Objects.equals(paymentAmount, that.paymentAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hash, clientId, oneTimePayment, diploma, paymentAmount);
    }
}
