package com.ewp.crm.service.interfaces;

import com.google.api.services.calendar.Calendar;

import java.io.IOException;
import java.util.Date;

public interface GoogleCalendarService {

    void addEvent(String calendarMentor, Date startDate) throws IOException;

    void update(Date newDate, Date oldDate, String calendarMentor);

    String authorize();

    Calendar tokenResponse(String code);

    boolean checkDate(Date newDate, String calendarMentor);

}
