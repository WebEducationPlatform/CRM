package com.ewp.crm.models.dto;

import com.ewp.crm.models.Role;

import java.util.List;

public class StatusDtoForBoard {

    private Long id;
    private String name;
    private Boolean isInvisible = false;
    private boolean createStudent;
    private List<ClientDtoForBoard> clients;
    private Long position;
    private List<Role> role;
    private Integer trialOffset = 0;
    private Integer nextPaymentOffset = 0;
    private boolean isFiltering =false;

    public StatusDtoForBoard() {
    }

    public StatusDtoForBoard(Long id,
                             String name,
                             Boolean isInvisible,
                             boolean createStudent,
                             List<ClientDtoForBoard> clients,
                             Long position,
                             List<Role> role,
                             Integer trialOffset,
                             Integer nextPaymentOffset,
                             boolean isFiltering) {
        this.id = id;
        this.name = name;
        this.isInvisible = isInvisible;
        this.createStudent = createStudent;
        this.clients = clients;
        this.position = position;
        this.role = role;
        this.trialOffset = trialOffset;
        this.nextPaymentOffset = nextPaymentOffset;
        this.isFiltering = isFiltering;
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

    public Boolean getInvisible() {
        return isInvisible;
    }

    public void setInvisible(Boolean invisible) {
        isInvisible = invisible;
    }

    public boolean isCreateStudent() {
        return createStudent;
    }

    public void setCreateStudent(boolean createStudent) {
        this.createStudent = createStudent;
    }

    public List<ClientDtoForBoard> getClients() {
        return clients;
    }

    public void setClients(List<ClientDtoForBoard> clients) {
        this.clients = clients;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public List<Role> getRole() {
        return role;
    }

    public void setRole(List<Role> role) {
        this.role = role;
    }

    public Integer getTrialOffset() {
        return trialOffset;
    }

    public void setTrialOffset(Integer trialOffset) {
        this.trialOffset = trialOffset;
    }

    public Integer getNextPaymentOffset() {
        return nextPaymentOffset;
    }

    public void setNextPaymentOffset(Integer nextPaymentOffset) {
        this.nextPaymentOffset = nextPaymentOffset;
    }

    public boolean getisFiltering() {
        return isFiltering;
    }

    public void setFiltering(boolean filtering) {
        isFiltering = filtering;
    }
}