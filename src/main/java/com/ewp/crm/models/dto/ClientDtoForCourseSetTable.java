package com.ewp.crm.models.dto;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Status;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ewp.crm.util.Constants.EMPTY_STRING;

public class ClientDtoForCourseSetTable {

        private Long id;
        private String name;
        private String lastName;
        private String email;
        private String phoneNumber;
        private String city;
        private String country;
        private Status status;

        public ClientDtoForCourseSetTable() {
        }

        public ClientDtoForCourseSetTable(Long id,
                                 String name,
                                 String lastName,
                                 String email,
                                 String phoneNumber,
                                 String city,
                                 String country,
                                 Status status) {
            this.id = id;
            this.name = name;
            this.lastName = lastName;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.status = status;
            this.city = city;
            this.country = country;

        }

        public Status getStatus() {return status; }

        public void setStatus(Status status) {
            this.status = status;
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

        private static com.ewp.crm.models.dto.ClientDtoForCourseSetTable getDtoClient(Client client) {

            final com.ewp.crm.models.dto.ClientDtoForCourseSetTable clientDtoForCourseSetTable = new com.ewp.crm.models.dto.ClientDtoForCourseSetTable();
            clientDtoForCourseSetTable.id = client.getId();
            clientDtoForCourseSetTable.name = client.getName();
            clientDtoForCourseSetTable.lastName = client.getLastName();
            clientDtoForCourseSetTable.status = client.getStatus();

            final Optional<String> emailOptional = client.getEmail();
            clientDtoForCourseSetTable.email = emailOptional.orElse(EMPTY_STRING);
            final Optional<String> phoneNumberOptional = client.getPhoneNumber();
            clientDtoForCourseSetTable.phoneNumber = phoneNumberOptional.orElse(EMPTY_STRING);

            clientDtoForCourseSetTable.city = client.getCity();
            clientDtoForCourseSetTable.country = client.getCountry();

            return clientDtoForCourseSetTable;
        }

        public static List<com.ewp.crm.models.dto.ClientDtoForCourseSetTable> getListDtoClients(List<Client> clients) {
            return clients.stream()
                    .map(com.ewp.crm.models.dto.ClientDtoForCourseSetTable::getDtoClient)
                    .collect(Collectors.toList());
        }

}
