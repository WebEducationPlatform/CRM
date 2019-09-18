package com.ewp.crm.models.dto.for_page_all_student;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.models.Status;
import com.ewp.crm.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClientDtoForAllStudentsDto {

    private long id;

    private String name;

    private String lastName;

    private String phoneNumber;

    private Status status;

    private String email;

    private List<SocialProfile> socialProfiles = new ArrayList<>();

    public ClientDtoForAllStudentsDto() {
    }

    public ClientDtoForAllStudentsDto(long id, String name, String lastName, String phoneNumber, String email, List<SocialProfile> socialProfiles) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
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

    public List<SocialProfile> getSocialProfiles() {
        return socialProfiles;
    }

    public void setSocialProfiles(List<SocialProfile> socialProfiles) {
        this.socialProfiles = socialProfiles;
    }

    public static interface ClientTransformerDto {

        long getClient_id();

        String getFirst_name();

        String getLast_name();

        String getClient_email();
    }

    /**
     * Данный метод получает клиента и на выходе дает его же, необходим для работы с - StudentDtoForAllStudentsDto.java,
     * @param client - получаемый клиент,
     * @return - возвращаемый клиент.
     */
    public static ClientDtoForAllStudentsDto getClientDtoForAllStudent(Client client) {
        ClientDtoForAllStudentsDto clientDtoForAllStudentsDto = new ClientDtoForAllStudentsDto();

        clientDtoForAllStudentsDto.id = client.getId();
        clientDtoForAllStudentsDto.name = client.getName();
        clientDtoForAllStudentsDto.lastName = client.getLastName();

        Optional<String> emailOptional = client.getEmail();
        clientDtoForAllStudentsDto.email = emailOptional.orElse(Constants.EMPTY_STRING);
        Optional<String> phoneNumberOptional = client.getPhoneNumber();
        clientDtoForAllStudentsDto.phoneNumber = phoneNumberOptional.orElse(Constants.EMPTY_STRING);

        clientDtoForAllStudentsDto.socialProfiles = client.getSocialProfiles();
        clientDtoForAllStudentsDto.status = client.getStatus();

        return clientDtoForAllStudentsDto;
    }

    public static List<ClientDtoForAllStudentsDto> getListClientDtoForAllStudentDto(List<Client> clients) {
        return clients
                .stream()
                .map(ClientDtoForAllStudentsDto::getClientDtoForAllStudent)
                .collect(Collectors.toList());
    }


}
