package com.ewp.crm.service.interfaces;

import com.google.api.services.calendar.Calendar;

import java.io.IOException;

public interface GoogleCalendarService {

    void addEvent(String calendarMentor, Long startDate, String skype) throws IOException;

    void update(Long newDate, Long oldDate, String calendarMentor, String skype);

    String authorize();

    Calendar tokenResponse(String code);

    boolean checkFreeDate(Long newDate, String calendarMentor);

    void delete(Long oldDate, String calendarMentor);

}
