package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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

    @NotNull
    @Column(name = "list_name")
    private String listName;

    @ManyToOne
    @JoinColumn(name = "mailing_type_id")
    @JoinTable(name = "list_mailing_types",
            joinColumns = {@JoinColumn(name = "list_mailing_id", foreignKey = @ForeignKey(name = "FK_LIST"))},
            inverseJoinColumns = {@JoinColumn(name = "mailing_type_id", foreignKey = @ForeignKey(name = "FK_TYPE"))})
    private ListMailingType type;

    @ElementCollection
    private List<String> recipients;

    public ListMailing() {
    }

    public ListMailing(String listName, List<String> recipients, ListMailingType listMailingType) {
        this.listName = listName;
        this.recipients = recipients;
        this.type = listMailingType;
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

    public ListMailingType getType() {
        return type;
    }

    public void setType(ListMailingType type) {
        this.type = type;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }
}
