package com.ewp.crm.models;

import javax.persistence.*;

@Entity
@Table(name = "google_token")
public class GoogleToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String value;

    public GoogleToken() {
    }

    public GoogleToken(String value) {
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
