package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonIgnore;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "phones_extra")
public class PhoneExtra implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Basic
    @Column(name = "phone_extra", unique = true)
    private String phoneExtra;

    @ManyToOne(targetEntity = Client.class)
    @JoinTable(name = "client_phone_extra",
            joinColumns = {@JoinColumn(name = "phone_extra_id", foreignKey = @ForeignKey(name = "FK_PHONE_EXTRA_CLIENT"))},
            inverseJoinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_PHONE_EXTRA"))})
    @JsonIgnore
    private Client client;

    public PhoneExtra() {
    }

    public PhoneExtra(String phoneExtra) {
        this.phoneExtra = phoneExtra;
    }

    public PhoneExtra(String phoneExtra, Client client) {
        this.phoneExtra = phoneExtra;
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneExtra() {
        return phoneExtra;
    }

    public void setPhoneExtra(String phoneExtra) {
        this.phoneExtra = phoneExtra;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhoneExtra)) return false;
        PhoneExtra that = (PhoneExtra) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(phoneExtra, that.phoneExtra) &&
                Objects.equals(client, that.client);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, phoneExtra, client);
    }
}
