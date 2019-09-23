package com.ewp.crm.models.dto;

import com.ewp.crm.models.Role;
import com.ewp.crm.models.SortedStatuses;
import com.ewp.crm.models.Status;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private Set<SortedStatuses> sortedStatuses = new HashSet<>();
    private Long templateId = 0L;

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
                             Long templateId) {
        this.id = id;
        this.name = name;
        this.isInvisible = isInvisible;
        this.createStudent = createStudent;
        this.clients = clients;
        this.position = position;
        this.role = role;
        this.trialOffset = trialOffset;
        this.nextPaymentOffset = nextPaymentOffset;
        this.templateId = templateId;
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

    public Set<SortedStatuses> getSortedStatuses() {
        return sortedStatuses;
    }

    public void setSortedStatuses(Set<SortedStatuses> sortedStatuses) {
        this.sortedStatuses = sortedStatuses;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public static StatusDtoForBoard getStatusDto(Status status) {

        StatusDtoForBoard statusDtoForBoard = new StatusDtoForBoard();

        statusDtoForBoard.id = status.getId();
        statusDtoForBoard.name = status.getName();
        statusDtoForBoard.isInvisible = status.getInvisible();
        statusDtoForBoard.createStudent = status.isCreateStudent();
        statusDtoForBoard.clients = ClientDtoForBoard.getListDtoClients(status.getClients());
        statusDtoForBoard.position = status.getPosition();
        statusDtoForBoard.role = status.getRole();
        statusDtoForBoard.trialOffset = status.getTrialOffset();
        statusDtoForBoard.nextPaymentOffset = status.getNextPaymentOffset();
        statusDtoForBoard.sortedStatuses = status.getSortedStatuses();
        statusDtoForBoard.templateId = status.getTemplateId();

        return statusDtoForBoard;
    }

    public static List<StatusDtoForBoard> getListDtoStatuses(List<Status> statuses) {
        return statuses
                .stream()
                .map(StatusDtoForBoard::getStatusDto)
                .collect(Collectors.toList());
    }

}
