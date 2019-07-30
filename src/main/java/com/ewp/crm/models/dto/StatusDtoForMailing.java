package com.ewp.crm.models.dto;

import com.ewp.crm.models.Status;

import java.util.List;
import java.util.stream.Collectors;

public class StatusDtoForMailing {

    private Long id;
    private String name;

    public StatusDtoForMailing() {
    }

    public StatusDtoForMailing(Long id, String name) {
        this.id = id;
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


    public static StatusDtoForMailing getStatusDto(Status status) {

        StatusDtoForMailing statusDtoForMailing = new StatusDtoForMailing();

        statusDtoForMailing.id = status.getId();
        statusDtoForMailing.name = status.getName();

        return statusDtoForMailing;
    }

    public static List<StatusDtoForMailing> getListDtoStatuses(List<Status> statuses) {
        return statuses
                .stream()
                .map(StatusDtoForMailing::getStatusDto)
                .collect(Collectors.toList());
    }

}
