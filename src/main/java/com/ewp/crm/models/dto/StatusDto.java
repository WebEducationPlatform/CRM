package com.ewp.crm.models.dto;

import com.ewp.crm.models.Status;

import java.util.List;
import java.util.stream.Collectors;

public class StatusDto {

    private Long id;
    private String name;

    public StatusDto() {
    }

    public StatusDto(Long id, String name) {
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


    public static StatusDto getStatusDto(Status status) {

        StatusDto statusDto = new StatusDto();

        statusDto.id = status.getId();
        statusDto.name = status.getName();

        return statusDto;
    }

    public static List<StatusDto> getListDtoStatuses(List<Status> statuses) {
        return statuses
                .stream()
                .map(StatusDto::getStatusDto)
                .collect(Collectors.toList());
    }

}
