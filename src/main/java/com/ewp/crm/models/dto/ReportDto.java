package com.ewp.crm.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Class for sending reports in a JSON format via ReportRestController
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ReportDto {

    private String message;
    private List<ClientDto> clients;

    public ReportDto(String message, List<ClientDto> clients) {
        this.message = message;
        this.clients = clients;
    }

    public ReportDto() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ClientDto> getClients() {
        return clients;
    }

    public void setClients(List<ClientDto> clients) {
        this.clients = clients;
    }
}
