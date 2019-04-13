package com.ewp.crm.models;

import javax.persistence.*;

@Entity
@Table(name = "slack_invite_links")
public class SlackInviteLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "hash", unique = true, nullable = false)
    private String hash;

    @JoinColumn (name = "client_id")
    @OneToOne
    private Client client;

    public SlackInviteLink() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
