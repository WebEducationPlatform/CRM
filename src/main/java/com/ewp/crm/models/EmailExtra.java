package com.ewp.crm.models;

import com.ewp.crm.utils.patterns.ValidationPattern;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "emails_extra")
public class EmailExtra  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_extra_id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Email(regexp = ValidationPattern.EMAIL_PATTERN)
    @Column(name = "email_extra", length = 50, unique = true)
    private String emailExtra;

    @ManyToOne(targetEntity = Client.class)
    @JoinTable(name = "client_email_extra",
            joinColumns = {@JoinColumn(name = "email_extra_id", foreignKey = @ForeignKey(name = "FK_EMAIL_EXTRA_CLIENT"))},
            inverseJoinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_EMAIL_EXTRA"))})
    @JsonIgnore
    private Client client;

    public EmailExtra(@NotNull @Size(max = 50) @Email(regexp = ValidationPattern.EMAIL_PATTERN) String emailExtra) {
        this.emailExtra = emailExtra;
    }

    public EmailExtra() {
    }

    public EmailExtra(@NotNull @Size(max = 50) @Email(regexp = ValidationPattern.EMAIL_PATTERN) String emailExtra, Client client) {
        this.emailExtra = emailExtra;
        this.client = client;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmailExtra() {
        return emailExtra;
    }

    public void setEmailExtra(String emailExtra) {
        this.emailExtra = emailExtra;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmailExtra)) return false;
        EmailExtra that = (EmailExtra) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(emailExtra, that.emailExtra) &&
                Objects.equals(client, that.client);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, emailExtra, client);
    }
}
