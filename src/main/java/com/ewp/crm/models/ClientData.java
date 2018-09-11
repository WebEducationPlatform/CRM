package com.ewp.crm.models;

import javax.persistence.*;

@Entity
@Table(name = "client_data")
public class ClientData {
    @Id
    @GeneratedValue
    @Column(name = "client_data_id")
    private Long id;

    @Column(name = "client_info")
    private String info;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinTable(name = "mailing_client_data",
            inverseJoinColumns = {@JoinColumn(name = "mailing_client_id", foreignKey = @ForeignKey(name = "FK_MAILING_MESSAGES"))},
            joinColumns = {@JoinColumn(name = "client_data_messages_id", foreignKey = @ForeignKey(name = "FK_CLIENT_DATA_MESSAGES"))}
    )
    private MailingMessage ownerMail;

    public ClientData() {}

    public ClientData(String info) {
        this.info = info;
    }

    public MailingMessage getOwnerMail() {
        return ownerMail;
    }

    public void setOwnerMail(MailingMessage ownerMail) {
        this.ownerMail = ownerMail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
