package com.ewp.crm.models;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "client_status_changing_history")
public class ClientStatusChangingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date", nullable = false)
    private ZonedDateTime date;

    @JoinColumn(name = "source_status_id")
    @ManyToOne(targetEntity = Status.class)
    private Status sourceStatus;

    @JoinColumn(name = "new_status_id", nullable = false)
    @ManyToOne(targetEntity = Status.class, optional = false)
    private Status newStatus;

    @Column(name = "is_fake", columnDefinition = "TINYINT(1) DEFAULT 0", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean isFake = false;

    @Column(name = "is_client_creation", columnDefinition = "TINYINT(1) DEFAULT 0", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean isClientCreation = false;

    @JoinColumn(name = "client_id", nullable = false)
    @ManyToOne(targetEntity = Client.class, optional = false)
    private Client client;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(targetEntity = User.class, optional = false)
    private User mover;

    public ClientStatusChangingHistory() {
    }

    public ClientStatusChangingHistory(ZonedDateTime date, Status sourceStatus, Status newStatus, Client client, User mover) {
        this.date = date;
        this.sourceStatus = sourceStatus;
        this.newStatus = newStatus;
        this.client = client;
        this.mover = mover;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public Status getSourceStatus() {
        return sourceStatus;
    }

    public void setSourceStatus(Status sourceStatus) {
        this.sourceStatus = sourceStatus;
    }

    public Status getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(Status newStatus) {
        this.newStatus = newStatus;
    }

    public Boolean getFake() {
        return isFake;
    }

    public void setFake(Boolean fake) {
        isFake = fake;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public User getMover() {
        return mover;
    }

    public void setMover(User mover) {
        this.mover = mover;
    }

    public Boolean getClientCreation() {
        return isClientCreation;
    }

    public void setClientCreation(Boolean clientCreation) {
        isClientCreation = clientCreation;
    }
}
