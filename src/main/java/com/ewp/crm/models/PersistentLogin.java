package com.ewp.crm.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="persistent_logins")
public class PersistentLogin {

    @Column(name = "username")
    private String username;

    @Id
    @Column(name = "series")
    private String series;

    @Column(name = "token")
    private String token;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_used")
    private Date lastUsed;

    public PersistentLogin() {
    }

    @PrePersist
    protected void onCreate() {
        lastUsed = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUsed = new Date();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Date lastUsed) {
        this.lastUsed = lastUsed;
    }
}
