package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "slack_profile")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SlackProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JsonProperty("real_name")
    @Column(name = "name")
    private String name;

    @JsonProperty("display_name")
    @Column(name = "display_name")
    private String displayName;

    @Column(name = "email", unique = true)
    private String email;

    @JsonIgnore
    @JoinColumn (name = "client_id")
    @OneToOne
    private Client client;

    @JsonProperty("hashName")
    @Column(name = "hash_name")
    private String hashName;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashName() {
        return hashName;
    }

    public void setHashName(String hashName) {
        this.hashName = hashName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlackProfile that = (SlackProfile) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(displayName, that.displayName) &&
                Objects.equals(email, that.email) &&
                Objects.equals(client, that.client) &&
                Objects.equals(hashName, that.hashName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, displayName, email, client, hashName);
    }

    @Override
    public String toString() {
        return "SlackProfile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", client=" + client +
                ", hashName='" + hashName + '\'' +
                '}';
    }
}
