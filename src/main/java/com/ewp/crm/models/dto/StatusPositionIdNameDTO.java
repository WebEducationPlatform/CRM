package com.ewp.crm.models.dto;

public class StatusPositionIdNameDTO {
    Long id;
    Long position;
    String statusName;


    public StatusPositionIdNameDTO(Long id, Long position, String statusName) {
        this.id = id;
        this.position = position;
        this.statusName = statusName;
    }

    public StatusPositionIdNameDTO() {
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}