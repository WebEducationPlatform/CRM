package com.ewp.crm.models.dto;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.models.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ewp.crm.util.Constants.EMPTY_STRING;

public class ClientDtoForBoard {

    private Long id;
    private String name;
    private String lastName;
    private User ownerUser;
    private boolean isHideCard;
    private String email;
    private String phoneNumber;
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
                             String email,
                             String phoneNumber,
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
        this.email = email;
        this.phoneNumber = phoneNumber;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    private static ClientDtoForBoard getDtoClient(Client client) {

        final ClientDtoForBoard clientDtoForBoard = new ClientDtoForBoard();
        clientDtoForBoard.id = client.getId();
        clientDtoForBoard.name = client.getName();
        clientDtoForBoard.lastName = client.getLastName();
        clientDtoForBoard.ownerUser = client.getOwnerUser();
        clientDtoForBoard.isHideCard = client.isHideCard();

        final Optional<String> emailOptional = client.getEmail();
        clientDtoForBoard.email = emailOptional.orElse(EMPTY_STRING);
        final Optional<String> phoneNumberOptional = client.getPhoneNumber();
        clientDtoForBoard.phoneNumber = phoneNumberOptional.orElse(EMPTY_STRING);

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
