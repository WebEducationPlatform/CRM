package com.ewp.crm.models.dto;

import com.ewp.crm.models.User;

public class ClientDtoForBoard {
    private Long id;
    private String name;
    private String lastName;
    private User ownerUser;
    private boolean isHideCard;
    private User ownerMentor;
    private String email;
    private String phone;

    public ClientDtoForBoard() {
    }

    public ClientDtoForBoard(Long id) {
        this.id = id;
    }

    public ClientDtoForBoard(Long id,
                             String name,
                             String lastName,
                             User ownerUser,
                             boolean isHideCard,
                             User ownerMentor,
                             String email,
                             String phone) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.ownerUser = ownerUser;
        this.isHideCard = isHideCard;
        this.ownerMentor = ownerMentor;
        this.email = email;
        this.phone = phone;
    }

    public User getOwnerMentor() {
        return ownerMentor;
    }

    public void setOwnerMentor(User ownerMentor) {
        this.ownerMentor = ownerMentor;
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public User getOwnerUser() {
        return ownerUser;
    }

    public void setOwnerUser(User ownerUser) {
        this.ownerUser = ownerUser;
    }

    public boolean isHideCard() {
        return isHideCard;
    }

    public void setHideCard(boolean hideCard) {
        isHideCard = hideCard;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}