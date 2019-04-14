package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.google.api.services.calendar.Calendar;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Optional;

public interface GoogleCalendarService {

    Optional<String> addCalendarEvent(ZonedDateTime eventStart, Client client);

    void updateCalendarEvent(String eventId, ZonedDateTime eventStart, Client client);

    void deleteCalendarEvent(String eventId);

    Calendar calendarBuilder();

    Calendar getCalendarBuilder();

    boolean googleAuthorizationIsNotNull();

    void addEvent(String calendarMentor, Long startDate, Client skype) throws IOException;

    void update(Long newDate, Long oldDate, String calendarMentor, Client skype) throws IOException;

    boolean checkFreeDateAndCorrectEmail(Long newDate, String calendarMentor);

    void delete(Long oldDate, String calendarMentor);

}
