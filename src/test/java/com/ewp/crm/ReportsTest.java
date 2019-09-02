package com.ewp.crm;

import com.ewp.crm.models.*;
import com.ewp.crm.models.dto.ClientDto;
import com.ewp.crm.models.dto.ReportDto;
import com.ewp.crm.service.interfaces.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings({"OptionalGetWithoutIsPresent", "WeakerAccess"})
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ReportsTest {

    @Autowired
    private ClientService clientService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private StatusService statusService;
    @Autowired
    private UserService userService;
    @Autowired
    private ClientStatusChangingHistoryService clientStatusChangingHistoryService;

    @BeforeEach
    void setUp() {
        init();
    }

    @AfterEach
    void removeAll() {
        clientService.delete(clientService.getClientByEmail("u.dolg@mail.ru").get());
        clientService.delete(clientService.getClientByEmail("vboyko@mail.ru").get());
        userService.delete(userService.getUserByEmail("owner@gmail.com").get());
    }

    @Test
    void whenFromAnyStatusToLearningStatus() {
        boolean result = false;
        Client client = clientService.getClientByEmail("u.dolg@mail.ru").get();
        Status lastStatus = statusService.get(1L).get();
        Status newStatus = statusService.get(4L).get();
        User user = userService.getUserByEmail("owner@gmail.com").get();
        ClientStatusChangingHistory clientStatusChangingHistory = new ClientStatusChangingHistory(
                getZonedDateTimeFromString("2019-08-22"),
                lastStatus,
                newStatus,
                client,
                user);
        clientStatusChangingHistoryService.add(clientStatusChangingHistory);
        ReportDto reportDto = reportService.getAllChangedStatusClientsByDate(getZonedDateTimeFromString("2019-08-20"),
                getZonedDateTimeFromString("2019-08-24"), 4L, null);
        List<ClientDto> clientDtoList = reportDto.getClients();
        for (ClientDto clientDto : clientDtoList) {
            if (clientDto.getEmail().equals("u.dolg@mail.ru")) {
                result = true;
                break;
            }
        }
        assertTrue(result);
        clientStatusChangingHistoryService.delete(clientStatusChangingHistory);
    }

    @Test
    void whenFromLearningStatusToRejectStatus() {
        boolean result = false;
        Client client = clientService.getClientByEmail("u.dolg@mail.ru").get();
        Status lastStatus = statusService.get(1L).get();
        Status newStatus = statusService.get(4L).get();
        User user = userService.getUserByEmail("owner@gmail.com").get();
        ClientStatusChangingHistory clientStatusChangingHistory = new ClientStatusChangingHistory(
                getZonedDateTimeFromString("2019-08-21"),
                lastStatus,
                newStatus,
                client,
                user);
        clientStatusChangingHistoryService.add(clientStatusChangingHistory);
        Status newStatus1 = statusService.get(5L).get();
        ClientStatusChangingHistory clientStatusChangingHistory1 = new ClientStatusChangingHistory(
                getZonedDateTimeFromString("2019-08-23"),
                newStatus,
                newStatus1,
                client,
                user);
        clientStatusChangingHistoryService.add(clientStatusChangingHistory1);
        ReportDto reportDto = reportService.getAllChangedStatusClientsByDate(getZonedDateTimeFromString("2019-08-18"),
                getZonedDateTimeFromString("2019-08-25"), 4L, 5L, null);
        List<ClientDto> clientDtoList = reportDto.getClients();
        for (ClientDto clientDto : clientDtoList) {
            if (clientDto.getEmail().equals("u.dolg@mail.ru")) {
                result = true;
                break;
            }
        }
        assertTrue(result);
        clientStatusChangingHistoryService.delete(clientStatusChangingHistory);
        clientStatusChangingHistoryService.delete(clientStatusChangingHistory1);
    }

    @Test
    void whenNewClientsAppearance() {
        boolean result = false;
        int count = 0;
        ReportDto reportDto = reportService.getAllNewClientsByDate(getZonedDateTimeFromString("2019-08-20"),
                getZonedDateTimeFromString("2019-08-21"), null);
        List<ClientDto> clientDtoList = reportDto.getClients();
        for (ClientDto clientDto : clientDtoList) {
            if (clientDto.getEmail().equals("u.dolg@mail.ru")) {
                count++;
            } else if (clientDto.getEmail().equals("vboyko@mail.ru")) {
                count++;
            }
        }
        if (count == 2) {
            result = true;
        }
        assertTrue(result);
    }

    @Test
    void whenFirstPayments() {
        boolean result = false;
        Client client = clientService.getClientByEmail("u.dolg@mail.ru").get();
        Status lastStatus = statusService.get(1L).get();
        Status newStatus = statusService.get(4L).get();
        User user = userService.getUserByEmail("owner@gmail.com").get();
        ClientStatusChangingHistory clientStatusChangingHistory = new ClientStatusChangingHistory(
                getZonedDateTimeFromString("2019-08-21"),
                lastStatus,
                newStatus,
                client,
                user);
        clientStatusChangingHistoryService.add(clientStatusChangingHistory);
        ReportDto reportDto = reportService.getAllFirstPaymentClientsByDate(getZonedDateTimeFromString("2019-08-20"),
                getZonedDateTimeFromString("2019-08-22"), null);
        List<ClientDto> clientDtoList = reportDto.getClients();
        for (ClientDto clientDto : clientDtoList) {
            if (clientDto.getEmail().equals("u.dolg@mail.ru")) {
                result = true;
                break;
            }
        }
        assertTrue(result);
        clientStatusChangingHistoryService.delete(clientStatusChangingHistory);
    }

    private void init() {
        List<Role> listRole = new ArrayList<>();
        listRole.add(roleService.getRoleByName("OWNER"));
        User user = new User("Vasya", "Owner", LocalDate.of(1989, 4, 1), "1999999999", "owner@gmail.com",
                "owner", null, Client.Sex.MALE.toString(), "Dubna", "Russia", listRole,
                true, true);
        userService.add(user);

        Client.Builder clientBuilder1 = new Client.Builder("Юрий", "79999992288", "u.dolg@mail.ru");
        Client client1 = clientBuilder1.lastName("Долгоруков")
                .birthDate(LocalDate.parse("1995-09-24"))
                .sex(Client.Sex.MALE)
                .city("Тула")
                .country("Россия")
                .build();
        Status status = statusService.get(1L).get();
        client1.setStatus(status);
        client1.setDateOfRegistration(getZonedDateTimeFromString("2019-08-20"));
        clientService.addClient(client1, user);

        Client.Builder clientBuilder2 = new Client.Builder("Вадим", "89687745632", "vboyko@mail.ru");
        Client client2 = clientBuilder2.lastName("Бойко")
                .birthDate(LocalDate.parse("1989-08-04"))
                .sex(Client.Sex.MALE)
                .city("Тула")
                .country("Россия")
                .build();
        client2.setDateOfRegistration(getZonedDateTimeFromString("2019-08-20"));
        client2.setStatus(status);
        clientService.addClient(client2, user);
    }

    private ZonedDateTime getZonedDateTimeFromString(String date) {
        return ZonedDateTime.of(LocalDate.parse(date, DateTimeFormatter.ISO_DATE).atStartOfDay(), ZoneId.systemDefault());
    }
}
