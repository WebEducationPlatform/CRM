package com.ewp.crm.models;

import java.time.LocalDateTime;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "mailing_message")
public class MailingMessage {
    @Id
    @GeneratedValue
    @Column(name = "mailing_message_id")
    private Long id;

    @NotNull
    @Column(name = "type", nullable = false)
    private String type;

    @NotNull
    @Column(name = "text", nullable = false)
    @Lob
    private String text;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "mailing_client_data",
            joinColumns = {@JoinColumn(name = "mailing_client_id", foreignKey = @ForeignKey(name = "FK_MAILING_MESSAGES"))},
            inverseJoinColumns = {@JoinColumn(name = "client_data_messages_id", foreignKey = @ForeignKey(name = "FK_CLIENT_DATA_MESSAGES"))})
    private Set<ClientData> clientsData;

    @Column(name = "readed_message")
    private boolean readedMessage;

    public boolean isReadedMessage() {
        return readedMessage;
    }

    public void setReadedMessage(boolean readedMessage) {
        this.readedMessage = readedMessage;
    }

    public MailingMessage(){}

    public MailingMessage(String type, String text, Set<ClientData> clientsData, LocalDateTime date) {
        this.type = type;
        this.text = text;
        this.clientsData = clientsData;
        this.date = date;
        this.readedMessage = false;
    }

    public Set<ClientData> getClientsData() {
        return clientsData;
    }

    public void setClientsData(Set<ClientData> clientsData) {
        this.clientsData = clientsData;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
