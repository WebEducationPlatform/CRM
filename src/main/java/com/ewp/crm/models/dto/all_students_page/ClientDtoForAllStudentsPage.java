package com.ewp.crm.models.dto.all_students_page;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.models.dto.StatusDto;
import com.ewp.crm.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Этот класс нужен для страницы "Все страницы", чтобы собрать необходимую информацию о клиенте.
 * Поля клиента, которые используются в all-students-table.html:
 *  a) status
 *  b) name
 *  c) lastName
 *  d) email
 *  e) phoneNumber
 *  f) socialProfiles
 *  g) id
 */
public class ClientDtoForAllStudentsPage {

    private long id;
    private String name;
    private String lastName;
    private String phoneNumber;
    private StatusDto statusDto;
    private String email;
    private List<SocialProfile> socialProfiles = new ArrayList<>();


    public ClientDtoForAllStudentsPage() {
    }

    public ClientDtoForAllStudentsPage(long id, String name, String lastName, String phoneNumber, StatusDto statusDto, String email, List<SocialProfile> socialProfiles) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.statusDto = statusDto;
        this.email = email;
        this.socialProfiles = socialProfiles;
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

    public StatusDto getStatusDto() {
        return statusDto;
    }

    public void setStatusDto(StatusDto statusDto) {
        this.statusDto = statusDto;
    }

    public List<SocialProfile> getSocialProfiles() {
        return socialProfiles;
    }

    public void setSocialProfiles(List<SocialProfile> socialProfiles) {
        this.socialProfiles = socialProfiles;
    }

    /**
     * Данный метод получает клиента и на выходе дает DTO этого клиента, необходим для работы с - StudentDto.java,
     * @param client - получаемый клиент,
     * @return - возвращаемый клиент.
     */
    public static ClientDtoForAllStudentsPage getClientDtoForAllStudentsPage(Client client) {
        ClientDtoForAllStudentsPage clientDtoForAllStudentsPage = new ClientDtoForAllStudentsPage();

        clientDtoForAllStudentsPage.id = client.getId();
        clientDtoForAllStudentsPage.name = client.getName();
        clientDtoForAllStudentsPage.lastName = client.getLastName();

        Optional<String> emailOptional = client.getEmail();
        clientDtoForAllStudentsPage.email = emailOptional.orElse(Constants.EMPTY_STRING);
        Optional<String> phoneNumberOptional = client.getPhoneNumber();
        clientDtoForAllStudentsPage.phoneNumber = phoneNumberOptional.orElse(Constants.EMPTY_STRING);

        clientDtoForAllStudentsPage.socialProfiles = client.getSocialProfiles();
        clientDtoForAllStudentsPage.statusDto = StatusDto.getStatusDto(client.getStatus());

        return clientDtoForAllStudentsPage;
    }

    public static List<ClientDtoForAllStudentsPage> getListClientDtoForAllStudentsPage(List<Client> clients) {
        return clients
                .stream()
                .map(ClientDtoForAllStudentsPage::getClientDtoForAllStudentsPage)
                .collect(Collectors.toList());
    }
}
