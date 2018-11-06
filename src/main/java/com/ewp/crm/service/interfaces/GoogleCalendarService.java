package com.ewp.crm.service.interfaces;

import com.google.api.services.calendar.Calendar;

import java.io.IOException;

public interface GoogleCalendarService {

    String authorize();

    Calendar tokenResponse(String code);

    boolean googleAuthorizationIsNotNull();

    void addEvent(String calendarMentor, Long startDate, String skype) throws IOException;

    void update(Long newDate, Long oldDate, String calendarMentor, String skype) throws IOException;

    boolean checkFreeDate(Long newDate, String calendarMentor);

    void delete(Long oldDate, String calendarMentor);

}
