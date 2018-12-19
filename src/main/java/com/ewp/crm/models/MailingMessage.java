package com.ewp.crm.models;


import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "mailing_message")
public class MailingMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mailing_message_id")
    private Long id;

    @NotNull
    @Column(name = "type", nullable = false)
    private String type;

    @NotNull
    @Column(name = "text", nullable = false)
    @Lob
    private String text;

    @Column(name = "date")
    private LocalDateTime date;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "mailing_client_data",
            joinColumns = {@JoinColumn(name = "mailing_client_id", foreignKey = @ForeignKey(name = "FK_MAILING_MESSAGES"))},
            inverseJoinColumns = {@JoinColumn(name = "client_data_messages_id", foreignKey = @ForeignKey(name = "FK_CLIENT_DATA_MESSAGES"))})
    @JsonManagedReference
    private Set<ClientData> clientsData;

    @Column(name = "readed_message")
    private Boolean readedMessage;

    @Column(name = "vkType")
    private String vkType;

    @Column(name = "userID")
    private Long userId;

    public MailingMessage(){}

    public MailingMessage(String type, String text, Set<ClientData> clientsData, LocalDateTime date, long userId) {
        this.type = type;
        this.text = text;
        this.clientsData = clientsData;
        this.date = date;
        this.readedMessage = false;
        this.userId = userId;
    }

    public MailingMessage(String type, String text, Set<ClientData> clientsData, LocalDateTime date, String vkType, long userId) {
        this.type = type;
        this.text = text;
        this.clientsData = clientsData;
        this.date = date;
        this.readedMessage = false;
        this.vkType = vkType;
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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

    public String getVkType() {
        return vkType;
    }

    public void setVkType(String vkType) {
        this.vkType = vkType;
    }

    public boolean isReadedMessage() {
        return readedMessage;
    }

    public void setReadedMessage(boolean readedMessage) {
        this.readedMessage = readedMessage;
    }

}
