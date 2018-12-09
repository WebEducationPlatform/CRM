package com.ewp.crm.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "list_mailing")
public class ListMailing implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "list_mailing_id")
    private long id;

    @Column(name = "list_name")
    private String listName;

    @Column(name = "recipients_email")
    private String recipientsEmail;

    @Column(name = "recipients_sms")
    private String recipientsSms;

    @Column(name = "recipients_vk")
    private String recipientsVk;

    public ListMailing() {
    }

    public ListMailing(String listName, String recipientsEmail, String recipientsSms, String recipientsVk) {
        this.listName = listName;
        this.recipientsEmail = recipientsEmail;
        this.recipientsSms = recipientsSms;
        this.recipientsVk = recipientsVk;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRecipientsEmail() {
        return recipientsEmail;
    }

    public void setRecipientsEmail(String recipientsEmail) {
        this.recipientsEmail = recipientsEmail;
    }

    public String getRecipientsSms() {
        return recipientsSms;
    }

    public void setRecipientsSms(String recipientsSms) {
        this.recipientsSms = recipientsSms;
    }

    public String getRecipientsVk() {
        return recipientsVk;
    }

    public void setRecipientsVk(String recipientsVk) {
        this.recipientsVk = recipientsVk;
    }
}
