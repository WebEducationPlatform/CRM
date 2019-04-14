package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;

import java.time.ZonedDateTime;
import java.util.Optional;

public interface GoogleCalendarService {

    Optional<String> addCalendarEvent(ZonedDateTime eventStart, Client client);

    void updateCalendarEvent(String eventId, ZonedDateTime eventStart, Client client);

    void deleteCalendarEvent(String eventId);

}
