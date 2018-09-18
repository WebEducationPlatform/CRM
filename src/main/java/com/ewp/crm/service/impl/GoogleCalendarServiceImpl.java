package com.ewp.crm.service.impl;

import com.ewp.crm.configs.GoogleCalendarConfigImpl;
import com.ewp.crm.controllers.GoogleCalendarController;
import com.ewp.crm.service.interfaces.GoogleCalendarService;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Service
public class GoogleCalendarServiceImpl implements GoogleCalendarService {

    private static Logger logger = LoggerFactory.getLogger(GoogleCalendarController.class);
    private Calendar client;
    private final String APPLICATION_NAME = "client-app";
    private HttpTransport httpTransport;
    private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private String clientId;
    private String clientSecret;
    private String redirectURI;
    private GoogleClientSecrets clientSecrets;
    private GoogleAuthorizationCodeFlow flow;
    private Credential credential;

    @Autowired

    public GoogleCalendarServiceImpl(GoogleCalendarConfigImpl config) {
        this.clientId = config.getClientId();
        this.clientSecret = config.getClientSecret();
        this.redirectURI = config.getRedirectURI();
    }

    @Override
    public String authorize() {
        AuthorizationCodeRequestUrl authorizationUrl;
        if (flow == null) {
            GoogleClientSecrets.Details web = new GoogleClientSecrets.Details();
            web.setClientId(clientId);
            web.setClientSecret(clientSecret);
            clientSecrets = new GoogleClientSecrets().setWeb(web);
            try {
                httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets,
                    Collections.singleton(CalendarScopes.CALENDAR)).setAccessType("offline").build();
        }
        authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectURI);
        return authorizationUrl.build();
    }

    @Override
    public Calendar tokenResponse(String code) {
        try {
            TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectURI).execute();
            credential = flow.createAndStoreCredential(response, "userID");
            return client = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME).build();

        } catch (Exception e) {
            logger.warn("Exception while handling OAuth2 callback (" + e.getMessage() + ")."
                    + " Redirecting to google connection status page.");
        }
        return client;
    }

    @Override
    public void addEvent(String calendarMentor, Date startDate) throws IOException {
        Event event = newEvent(startDate);
        com.google.api.services.calendar.model.Calendar calendar =
                client.calendars().get(calendarMentor).execute();
        client.events().insert(calendar.getId(), event).execute();
    }

    @Override
    public void update(Date newDate, Date oldDate, String calendarMentor) {
        try {
            com.google.api.services.calendar.model.Calendar calendar =
                    client.calendars().get(calendarMentor).execute();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("y-MM-dd'T'HH:mm:ss");
            List<Event> eventAll = client.events().list(calendar.getId()).execute().getItems();
            Event newEvent = newEvent(newDate);

            for (int i = 0; i < eventAll.size(); i++) {
                if (eventAll.get(i).getStart().toString().contains(dateFormatter.format(oldDate))) {
                    String eventId = eventAll.get(i).getId();
                    client.events().delete(calendarMentor, eventId).execute();
                    client.events().insert(calendar.getId(), newEvent).execute();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean checkDate(Date date, String calendarMentor) {
        try {
            com.google.api.services.calendar.model.Calendar calendar =
                    client.calendars().get(calendarMentor).execute();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("y-MM-dd'T'HH:mm:ss");
            List<Event> eventAll = client.events().list(calendar.getId()).execute().getItems();

            for (int i = 0; i < eventAll.size(); i++) {
                if (eventAll.get(i).getStart().toString().contains(dateFormatter.format(date))) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Event newEvent(Date startDate) {
        Event event = new Event();
        event.setSummary("Skype беседа");
        Date endDate = new Date(startDate.getTime() + 3600000);
        DateTime start = new DateTime(startDate, TimeZone.getTimeZone("UTC"));
        event.setStart(new EventDateTime().setDateTime(start));
        DateTime end = new DateTime(endDate, TimeZone.getTimeZone("UTC"));
        event.setEnd(new EventDateTime().setDateTime(end));
        return event;
    }

}
