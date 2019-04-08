package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "list_mailing_type")
public class ListMailingType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mailing_type_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "list_mailing_types",
            joinColumns = {@JoinColumn(name = "mailing_type_id", foreignKey = @ForeignKey(name = "FK_TYPE"))},
            inverseJoinColumns = {@JoinColumn(name = "list_mailing_id", foreignKey = @ForeignKey(name = "FK_LIST"))})
    private List<ListMailing> listMailings;

    public ListMailingType() {
    }

    public ListMailingType(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ListMailing> getListMailings() {
        return listMailings;
    }

    public void setListMailings(List<ListMailing> listMailings) {
        this.listMailings = listMailings;
    }
}
