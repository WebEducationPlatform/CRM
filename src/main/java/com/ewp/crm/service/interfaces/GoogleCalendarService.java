package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.google.api.services.calendar.Calendar;

import java.io.IOException;

public interface GoogleCalendarService {

    /*String authorize();

    Calendar tokenResponse(String code);*/
    Calendar calendarBuilder();

    Calendar getCalendarBuilder();

    boolean googleAuthorizationIsNotNull();

    void addEvent(String calendarMentor, Long startDate, Client skype) throws IOException;

    void update(Long newDate, Long oldDate, String calendarMentor, Client skype) throws IOException;

    boolean checkFreeDateAndCorrectEmail(Long newDate, String calendarMentor);

    void delete(Long oldDate, String calendarMentor);

}
