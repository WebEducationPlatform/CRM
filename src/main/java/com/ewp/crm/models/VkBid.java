package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@Table(name = "vk_bid")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class VkBid {
    @Id
    @GeneratedValue
    @Column(name = "id")
    long id;
    @Column(name = "number")
    int number;
    @Column(name = "name")
    String name;
    @Column(name = "type")
    String type;

    public VkBid(int number, String name, String type) {
        this.number = number;
        this.name = name;
        this.type = type;
    }

    public VkBid() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
