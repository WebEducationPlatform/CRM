package com.ewp.crm.models.dto;

import com.ewp.crm.models.Client;

public class ClientDto {

    private long id;

    private String name;

    private String lastName;

    private String phoneNumber;

    private String email;

    public ClientDto() {
    }

    public ClientDto(long id, String name, String lastName, String phoneNumber, String email) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static interface ClientTransformer {
        long getClient_id();

        String getFirst_name();

        String getLast_name();

        String getClient_email();
    }

    public static ClientDto getClientDto(Client client){

        ClientDto clientDto = new ClientDto();
        clientDto.id = client.getId();
        clientDto.name =  client.getName();
        clientDto.lastName =  client.getLastName();
        clientDto.phoneNumber =  client.getPhoneNumber().isPresent() ? client.getPhoneNumber().get(): "";
        clientDto.email =  client.getEmail().isPresent() ? client.getEmail().get() : "";
        return clientDto;
    }
}
