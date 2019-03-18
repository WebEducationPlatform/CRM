package com.ewp.crm.service.google;

import com.ewp.crm.models.Client;
import com.ewp.crm.service.interfaces.GoogleAuthorizationService;
import com.ewp.crm.service.interfaces.GoogleCalendarService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class GoogleCalendarServiceImpl implements GoogleCalendarService {

	private static Logger logger = LoggerFactory.getLogger(GoogleCalendarServiceImpl.class);
	private Calendar calendarBuilder;
	private final String APPLICATION_NAME = "client-app";
	private HttpTransport httpTransport;
	private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	private Credential credential;

	private final GoogleAuthorizationService authorizationService;

	@Autowired
	public GoogleCalendarServiceImpl(GoogleAuthorizationService authorizationService) {
		this.authorizationService = authorizationService;
	}

	@Override
	public Calendar calendarBuilder() {
		credential = authorizationService.getCredential();
		return calendarBuilder = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME).build();
	}

	@Override
	public Calendar getCalendarBuilder() {
		return calendarBuilder;
	}

	@Override
	public boolean googleAuthorizationIsNotNull(){
		return calendarBuilder != null;
	}

	@Override
	public boolean checkFreeDateAndCorrectEmail(Long date, String calendarMentor) {
		try {
			com.google.api.services.calendar.model.Calendar calendar = calendarBuilder.calendars().get(calendarMentor).execute();
			List<Event> eventAll = calendarBuilder.events().list(calendar.getId()).execute().getItems();
			String format = getFormatZonedDateTime(date, calendar);
			for (Event anEventAll : eventAll) {
				if (anEventAll.getStart().toString().contains(format)) {
					return true;
				}
			}
		} catch (IOException e) {
			logger.error("Error to send message ", e);
		}
		return false;
	}

	@Override
	public void addEvent(String calendarMentor, Long startDate, Client client) throws IOException {
		Event event = newEvent(startDate, client);
		com.google.api.services.calendar.model.Calendar calendar =
				calendarBuilder.calendars().get(calendarMentor).execute();
		calendarBuilder.events().insert(calendar.getId(), event).execute();
	}

	@Override
	public void update(Long newDate, Long oldDate, String calendarMentor, Client client) throws IOException {
		com.google.api.services.calendar.model.Calendar calendar =
				calendarBuilder.calendars().get(calendarMentor).execute();
		String formattedDateOld = getFormatZonedDateTime(oldDate, calendar);
		Event newEvent = newEvent(newDate, client);
		List<Event> eventAll = calendarBuilder.events().list(calendar.getId()).execute().getItems();
		for (int i = 0; i < eventAll.size(); i++) {
			if (eventAll.get(i).getStart().toString().contains(formattedDateOld)) {
				String eventId = eventAll.get(i).getId();
				calendarBuilder.events().delete(calendarMentor, eventId).execute();
				calendarBuilder.events().insert(calendar.getId(), newEvent).execute();
			}
		}
	}

	@Override
	public void delete(Long oldDate, String calendarMentor) {
		try {
			com.google.api.services.calendar.model.Calendar calendar =
					calendarBuilder.calendars().get(calendarMentor).execute();
			String format = getFormatZonedDateTime(oldDate, calendar);
			List<Event> eventAll = calendarBuilder.events().list(calendar.getId()).execute().getItems();
			for (int i = 0; i < eventAll.size(); i++) {
				if (eventAll.get(i).getStart().toString().contains(format)) {
					String eventId = eventAll.get(i).getId();
					calendarBuilder.events().delete(calendarMentor, eventId).execute();
				}
			}
		} catch (IOException e) {
			logger.error("Error to send message ", e);
		}
	}

	private Event newEvent(Long startDate, Client client) {
		Event event = new Event();
		event.setSummary("CRM - " + client.getName() + " " + client.getLastName() + " (Skype: " + client.getSkype() + ")");
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		String format = Instant.ofEpochMilli(startDate)
				.atZone(ZoneId.of("+00:00"))
				.withZoneSameLocal(ZoneId.of("Europe/Moscow"))
				.format(outputFormatter);
		DateTime start = new DateTime(format);
		event.setStart(new EventDateTime().setDateTime(start));
		DateTime end = new DateTime(format);
		event.setEnd(new EventDateTime().setDateTime(end));
		return event;
	}

	private String getFormatZonedDateTime(Long date, com.google.api.services.calendar.model.Calendar calendar) {
		return Instant.ofEpochMilli(date)
				.atZone(ZoneId.of("+00:00"))
				.withZoneSameLocal(ZoneId.of("Europe/Moscow"))
				.withZoneSameInstant(ZoneId.of(calendar.getTimeZone()))
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
	}
}
