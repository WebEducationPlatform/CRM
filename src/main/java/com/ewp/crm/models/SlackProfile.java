package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity
@Table(name = "slack_profile")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SlackProfile {

    @Id
    @GeneratedValue
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

    @PrimaryKeyJoinColumn
    @OneToOne
    private Client client;

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
}
