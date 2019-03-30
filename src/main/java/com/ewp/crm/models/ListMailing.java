package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "list_mailing")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ListMailing implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "list_mailing_id")
    private Long id;

    @Column(name = "list_name")
    private String listName;

    @ElementCollection
    private List<String> recipientsEmail;

    @ElementCollection
    private List<String> recipientsSms;

    @ElementCollection
    private List<String> recipientsVk;

    @ElementCollection
    private List<String> recipientsSlack;

    public ListMailing() {
    }

    public ListMailing(String listName, List<String> recipientsEmail, List<String> recipientsSms, List<String> recipientsVk, List<String> recipientsSlack) {
        this.listName = listName;
        this.recipientsEmail = recipientsEmail;
        this.recipientsSms = recipientsSms;
        this.recipientsVk = recipientsVk;
        this.recipientsSlack = recipientsSlack;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public List<String> getRecipientsEmail() {
        return recipientsEmail;
    }

    public void setRecipientsEmail(List<String> recipientsEmail) {
        this.recipientsEmail = recipientsEmail;
    }

    public List<String> getRecipientsSms() {
        return recipientsSms;
    }

    public void setRecipientsSms(List<String> recipientsSms) {
        this.recipientsSms = recipientsSms;
    }

    public List<String> getRecipientsVk() {
        return recipientsVk;
    }

    public void setRecipientsVk(List<String> recipientsVk) {
        this.recipientsVk = recipientsVk;
    }

    public List<String> getRecipientsSlack() {
        return recipientsSlack;
    }

    public void setRecipientsSlack(List<String> recipientsSlack) {
        this.recipientsSlack = recipientsSlack;
    }
}
