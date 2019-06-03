package com.ewp.crm.models.dto;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.models.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClientDtoForBoard {
    private Long id;
    private String name;
    private String lastName;
    private User ownerUser;
    private boolean isHideCard;
    private List<String> clientEmails;
    private List<String> clientPhones;
    private String skype;
    private String city;
    private String country;
    private List<SocialProfile> socialProfiles;
    private User ownerMentor;

    public ClientDtoForBoard() {
    }

    public ClientDtoForBoard(Long id,
                             String name,
                             String lastName,
                             User ownerUser,
                             boolean isHideCard,
                             List<String> clientEmails,
                             List<String> clientPhones,
                             String skype,
                             String city,
                             String country,
                             List<SocialProfile> socialProfiles,
                             User ownerMentor) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.ownerUser = ownerUser;
        this.isHideCard = isHideCard;
        this.clientEmails = clientEmails;
        this.clientPhones = clientPhones;
        this.skype = skype;
        this.city = city;
        this.country = country;
        this.socialProfiles = socialProfiles;
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

    public List<String> getClientEmails() {
        return clientEmails;
    }

    public void setClientEmails(List<String> clientEmails) {
        this.clientEmails = clientEmails;
    }

    public List<String> getClientPhones() {
        return clientPhones;
    }

    public void setClientPhones(List<String> clientPhones) {
        this.clientPhones = clientPhones;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<SocialProfile> getSocialProfiles() {
        return socialProfiles;
    }

    public void setSocialProfiles(List<SocialProfile> socialProfiles) {
        this.socialProfiles = socialProfiles;
    }

    public Optional<String> getEmail() {
        return clientEmails.isEmpty() ? Optional.empty() : Optional.ofNullable(clientEmails.get(0));
    }

    public Optional<String> getPhoneNumber() {
        return clientPhones.isEmpty() ? Optional.empty() : Optional.ofNullable(clientPhones.get(0));
    }



    private static ClientDtoForBoard getDtoClient(Client client) {

        ClientDtoForBoard clientDtoForBoard = new ClientDtoForBoard();
        clientDtoForBoard.id = client.getId();
        clientDtoForBoard.name = client.getName();
        clientDtoForBoard.lastName = client.getLastName();
        clientDtoForBoard.ownerUser = client.getOwnerUser();
        clientDtoForBoard.isHideCard = client.isHideCard();
        clientDtoForBoard.clientEmails = client.getClientEmails();
        clientDtoForBoard.clientPhones = client.getClientPhones();
        clientDtoForBoard.skype = client.getSkype();
        clientDtoForBoard.city = client.getCity();
        clientDtoForBoard.country = client.getCountry();
        clientDtoForBoard.socialProfiles = client.getSocialProfiles();
        clientDtoForBoard.ownerMentor = client.getOwnerMentor();

        return clientDtoForBoard;
    }

    public static List<ClientDtoForBoard> getListDtoClients(List<Client> clients) {
        return clients.stream()
                .map(ClientDtoForBoard::getDtoClient)
                .collect(Collectors.toList());
    }
}
