package com.ewp.crm.models.dto;

import com.ewp.crm.models.CallRecord;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.Role;
import com.ewp.crm.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HrDtoForBoardTest {

    private List<User> users = new ArrayList<>();

    @BeforeEach
    void setUp() {
        init();
    }

    @Test
    void whenUserOneCreated_ThenAvgCallsPerDayIsTwo() {
        // given

        // when
        HrDtoForBoard hrDto = new HrDtoForBoard(users.get(0));
        long result = hrDto.getAvgCallsPerDay();

        // then
        assertEquals(2L, result, "Wrong average of calls");
    }

    private void init() {
        User user1 = new User("Vasya", "Hr", LocalDate.of(1989, 4, 1), "1999999999", "hr1@gmail.com",
                "hr1", null, Client.Sex.MALE.toString(), "Dubna", "Russia",
                Collections.singletonList(new Role("HR")), true, true);

        User user2 = new User("Petya", "Hr", LocalDate.of(1998, 7, 9), "2999999999", "hr2@gmail.com",
                "hr2", null, Client.Sex.MALE.toString(), "Novgorod", "Russia",
                Collections.singletonList(new Role("HR")), true, true);

        User user3 = new User("Dasha", "Hr", LocalDate.of(1984, 3, 12), "3999999999", "hr3@gmail.com",
                "hr3", null, Client.Sex.FEMALE.toString(), "Samara", "Russia",
                Collections.singletonList(new Role("HR")), true, true);

        Client.Builder clientBuilder1 = new Client.Builder("Юрий", "79999992288", "u.dolg@mail.ru");
        Client client1 = clientBuilder1.lastName("Долгоруков")
                .birthDate(LocalDate.parse("1995-09-24"))
                .sex(Client.Sex.MALE)
                .city("Тула")
                .country("Россия")
                .build();
        client1.setState(Client.State.NEW);

        Client.Builder clientBuilder2 = new Client.Builder("Вадим", "89687745632", "vboyko@mail.ru");
        Client client2 = clientBuilder2.lastName("Бойко")
                .birthDate(LocalDate.parse("1989-08-04"))
                .sex(Client.Sex.MALE)
                .city("Тула")
                .country("Россия")
                .build();
        client2.setState(Client.State.NEW);
        client2.setDateOfRegistration(ZonedDateTime.ofInstant(Instant.now().minusMillis(200000000), ZoneId.systemDefault()));

        Client.Builder clientBuilder3 = new Client.Builder("Александра", "78300029530", "a.solo@mail.ru");
        Client client3 = clientBuilder3.lastName("Соловьева")
                .birthDate(LocalDate.parse("1975-03-10"))
                .sex(Client.Sex.FEMALE)
                .city("Тула")
                .country("Россия")
                .build();
        client3.setState(Client.State.NEW);

        user1.setClients(Collections.singletonList(client1));
        user2.setClients(Arrays.asList(client2, client3));
        user3.setClients(Arrays.asList(client1, client2, client3));

        CallRecord callRecord11 = new CallRecord();
        callRecord11.setDate(ZonedDateTime.of(LocalDate.of(2019, 5, 14), LocalTime.MIDNIGHT, ZoneId.systemDefault()));
        CallRecord callRecord12 = new CallRecord();
        callRecord12.setDate(ZonedDateTime.of(LocalDate.of(2019, 5, 14), LocalTime.of(10, 50), ZoneId.systemDefault()));
        CallRecord callRecord13 = new CallRecord();
        callRecord13.setDate(ZonedDateTime.of(LocalDate.of(2019, 5, 10), LocalTime.MIDNIGHT, ZoneId.systemDefault()));
        CallRecord callRecord14 = new CallRecord();
        callRecord14.setDate(ZonedDateTime.of(LocalDate.of(2019, 5, 10), LocalTime.of(11, 10), ZoneId.systemDefault()));
        user1.setCallRecords(Arrays.asList(callRecord11, callRecord12, callRecord13, callRecord14));

        CallRecord callRecord21 = new CallRecord();
        callRecord21.setDate(ZonedDateTime.of(LocalDate.of(2019, 5, 14), LocalTime.MIDNIGHT, ZoneId.systemDefault()));
        CallRecord callRecord22 = new CallRecord();
        callRecord22.setDate(ZonedDateTime.of(LocalDate.of(2019, 5, 14), LocalTime.of(10, 40), ZoneId.systemDefault()));
        user2.setCallRecords(Arrays.asList(callRecord21, callRecord22));

        users.addAll(Arrays.asList(user1, user2, user3));
    }

}