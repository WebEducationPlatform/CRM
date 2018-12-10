package com.ewp.crm.models;


import javax.persistence.*;

@Entity
@Table(name = "vk_token")
public class VkToken {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vk_id_token")
    private long id;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "name_sender")
    private String nameSender;

    @Column(name = "id_sender")
    private long idSender;


    public VkToken() {
    }

    public VkToken(String accessToken, String nameSender, long idSender) {
        this.accessToken = accessToken;
        this.nameSender = nameSender;
        this.idSender = idSender;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getNameSender() {
        return nameSender;
    }

    public void setNameSender(String nameSender) {
        this.nameSender = nameSender;
    }

    public long getIdSender() {
        return idSender;
    }

    public void setIdSender(long idSender) {
        this.idSender = idSender;
    }
}
