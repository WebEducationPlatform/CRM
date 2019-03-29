package com.ewp.crm.models;

import com.ewp.crm.utils.patterns.ValidationPattern;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "email_extra")
public class EmailExtra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_extra_id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Email(regexp = ValidationPattern.EMAIL_PATTERN)
    @Column(name = "email_extra", length = 50)
    private String email_extra;

    @JsonIgnore
    @ManyToOne(targetEntity = Client.class)
    @JoinTable(name = "client_comment",
            joinColumns = {@JoinColumn(name = "email_extra_id", foreignKey = @ForeignKey(name = "FK_EMAIL_EXTRA_CLIENT"))},
            inverseJoinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_EMAIL_EXTRA"))})
    private Client client;


    public EmailExtra(@NotNull @Size(max = 50) @Email(regexp = ValidationPattern.EMAIL_PATTERN) String email_extra) {
        this.email_extra = email_extra;
    }
}
