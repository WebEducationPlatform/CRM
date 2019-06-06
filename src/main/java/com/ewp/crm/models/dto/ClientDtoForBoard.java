package com.ewp.crm.models.dto;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.User;

import java.util.List;
import java.util.stream.Collectors;

public class ClientDtoForBoard {
    private Long id;
    private String name;
    private String lastName;
    private User ownerUser;
    private boolean isHideCard;
    private User ownerMentor;

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
                             User ownerMentor) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.ownerUser = ownerUser;
        this.isHideCard = isHideCard;
        this.ownerMentor = ownerMentor;
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

    private static ClientDtoForBoard getDtoClient(Client client) {
        ClientDtoForBoard clientDtoForBoard = new ClientDtoForBoard();
        clientDtoForBoard.id = client.getId();
        clientDtoForBoard.name = client.getName();
        clientDtoForBoard.lastName = client.getLastName();
        clientDtoForBoard.ownerUser = client.getOwnerUser();
        clientDtoForBoard.isHideCard = client.isHideCard();
        clientDtoForBoard.ownerMentor = client.getOwnerMentor();

        return clientDtoForBoard;
    }

    public static List<ClientDtoForBoard> getListDtoClients(List<Client> clients) {
        return clients.stream()
                .map(ClientDtoForBoard::getDtoClient)
                .collect(Collectors.toList());
    }
}