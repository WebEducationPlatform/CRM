package com.ewp.crm.models;

import java.util.List;

/**
 * Class for sending reports in a JSON format via ReportRestController
 */
public class Report {

    private String message;
    private List<Client> clients;

    public Report(String message, List<Client> clients) {
        this.message = message;
        this.clients = clients;
    }

    public Report() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }
}
