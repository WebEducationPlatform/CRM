package com.ewp.crm.service.google;

import com.ewp.crm.configs.GoogleAPIConfigImpl;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.GoogleToken;
import com.ewp.crm.service.interfaces.GoogleAuthorizationService;
import com.ewp.crm.service.interfaces.GoogleCalendarService;
import com.ewp.crm.service.interfaces.GoogleTokenService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ibm.icu.text.Transliterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class GoogleCalendarServiceImpl implements GoogleCalendarService {

    private final static String CYRILLIC_TO_LATIN = "Russian-Latin/BGN";
	private static Logger logger = LoggerFactory.getLogger(GoogleCalendarServiceImpl.class);
	private Calendar calendarBuilder;
	private final String APPLICATION_NAME = "client-app";
	private HttpTransport httpTransport;
	private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	private Credential credential;

	private final GoogleAuthorizationService authorizationService;
	private final GoogleTokenService tokenService;
	private final String calendarName;
	private final String eventName;

	@Autowired
	public GoogleCalendarServiceImpl(GoogleAuthorizationService authorizationService, GoogleTokenService tokenService,
                                     GoogleAPIConfigImpl config) {
		this.authorizationService = authorizationService;
		this.tokenService = tokenService;
		this.calendarName = config.getCalendarName();
		this.eventName = config.getEventName();
	}

    private HttpClient getHttpClient() {
        return HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build())
                .build();
    }

	private Optional<String> getAllCalendarsResponse() {
        Optional<GoogleToken> tokenOpt = tokenService.getRefreshedToken(GoogleTokenService.TokenType.CALENDAR);
        if (tokenOpt.isPresent()) {
            String token = tokenOpt.get().getAccessToken();
            String uri = "https://www.googleapis.com/calendar/v3/users/me/calendarList"
                    + "?access_token=" + token;
            try {
                HttpGet httpGet = new HttpGet(uri);
                HttpClient httpClient = getHttpClient();
                HttpResponse response = httpClient.execute(httpGet);
                String result = EntityUtils.toString(response.getEntity());
                return Optional.ofNullable(result);
            } catch (IOException e) {
                logger.error("Can't get calendars by uri " + uri, e);
            }
        }
        return Optional.empty();
    }

    private Optional<String> getCalendarIdByName(String name) {
	    Optional<String> calendarsJson = getAllCalendarsResponse();
	    if (calendarsJson.isPresent()) {
            try {
                JSONObject response = new JSONObject(calendarsJson.get());
                JSONArray calendars = response.getJSONArray("items");
                for (int i = 0; i < calendars.length(); i++) {
                    JSONObject calendar = calendars.getJSONObject(i);
                    if (name.equals(calendar.getString("summary"))) {
                        return Optional.ofNullable(calendar.optString("id"));
                    }
                }
            } catch (JSONException e) {
                logger.error("Can't parse calendars response: " + calendarsJson.get(), e);
            }
        }
        return Optional.empty();
    }

    private Optional<String> createCalendarEventRequestBody(ZonedDateTime eventStart, Client client) {
	    JsonObject root = new JsonObject();
        Transliterator toLatinTrans = Transliterator.getInstance(CYRILLIC_TO_LATIN);
        String summary = toLatinTrans.transliterate(String.format("%s %s", client.getLastName(), client.getName()));
        root.addProperty("summary", summary);
        root.addProperty("description", eventName);

	    JsonArray attendees = new JsonArray();
	    JsonObject clientAttendee = new JsonObject();
	    clientAttendee.addProperty("email", client.getEmail());
	    attendees.add(clientAttendee);

        JsonObject start = new JsonObject();
        start.addProperty("dateTime", new DateTime(eventStart.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)).toString());
        start.addProperty("timeZone","Europe/Moscow");

	    JsonObject end = new JsonObject();
	    end.addProperty("dateTime", new DateTime(eventStart.plusHours(1L).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)).toString());
	    end.addProperty("timeZone","Europe/Moscow");

        root.add("attendees", attendees);
        root.add("start", start);
        root.add("end", end);

        return Optional.ofNullable(root.toString());
    }

    @Override
    public Optional<String> addCalendarEvent(ZonedDateTime eventStart, Client client) {
	    Optional<String> calendarId = getCalendarIdByName(calendarName);
	    if (calendarId.isPresent()) {
            Optional<GoogleToken> tokenOpt = tokenService.getRefreshedToken(GoogleTokenService.TokenType.CALENDAR);
            if (tokenOpt.isPresent()) {
                String token = tokenOpt.get().getAccessToken();
                String uri = String.format("https://www.googleapis.com/calendar/v3/calendars/%s/events", calendarId.get())
                        + "?access_token=" + token +
                        "&sendUpdates=all";
                HttpPost request = new HttpPost(uri);
                request.addHeader("Content-Type", "application/json");
                EntityBuilder params = EntityBuilder.create();
                Optional<String> eventJson = createCalendarEventRequestBody(eventStart, client);
                if (eventJson.isPresent()) {
                    params.setText(eventJson.get());
                    request.setEntity(params.build());
                    HttpClient httpClient = getHttpClient();
                    String result = StringUtils.EMPTY;
                    try {
                        HttpResponse response = httpClient.execute(request);
                        result = EntityUtils.toString(response.getEntity());
                        JSONObject obj = new JSONObject(result);
                        return Optional.ofNullable(obj.optString("id"));
                    } catch (IOException e) {
                        logger.error("Can't send post request to calendar: " + uri, e);
                    } catch (JSONException e) {
                        logger.error("Can't parse json from calendar: " + result, e);
                    }
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void updateCalendarEvent(String eventId, ZonedDateTime eventStart, Client client) {
        Optional<String> calendarId = getCalendarIdByName(calendarName);
        if (calendarId.isPresent()) {
            Optional<GoogleToken> tokenOpt = tokenService.getRefreshedToken(GoogleTokenService.TokenType.CALENDAR);
            if (tokenOpt.isPresent()) {
                String token = tokenOpt.get().getAccessToken();
                String uri = String.format("https://www.googleapis.com/calendar/v3/calendars/%s/events/%s", calendarId.get(), eventId)
                        + "?access_token=" + token +
                        "&sendUpdates=all";
                HttpPut request = new HttpPut(uri);
                request.addHeader("Content-Type", "application/json");
                EntityBuilder params = EntityBuilder.create();
                Optional<String> eventJson = createCalendarEventRequestBody(eventStart, client);
                if (eventJson.isPresent()) {
                    params.setText(eventJson.get());
                    request.setEntity(params.build());
                    HttpClient httpClient = getHttpClient();
                    try {
                        HttpResponse response = httpClient.execute(request);
                    } catch (IOException e) {
                        logger.error("Can't send put request to calendar: " + uri, e);
                    }
                }
            }
        }
    }

    @Override
    public void deleteCalendarEvent(String eventId) {
        Optional<String> calendarId = getCalendarIdByName(calendarName);
        if (calendarId.isPresent()) {
            Optional<GoogleToken> tokenOpt = tokenService.getRefreshedToken(GoogleTokenService.TokenType.CALENDAR);
            if (tokenOpt.isPresent()) {
                String token = tokenOpt.get().getAccessToken();
                String uri = String.format("https://www.googleapis.com/calendar/v3/calendars/%s/events/%s", calendarId.get(), eventId)
                        + "?access_token=" + token +
                        "&sendUpdates=all";
                HttpDelete request = new HttpDelete(uri);
                HttpClient httpClient = getHttpClient();
                try {
                    HttpResponse response = httpClient.execute(request);
                } catch (IOException e) {
                    logger.error("Can't send delete request to calendar: " + uri, e);
                }
            }
        }
    }

	@Override
	public Calendar calendarBuilder() {
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            calendarBuilder = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME).build();
        } catch (GeneralSecurityException | IOException e) {
            logger.error("Error to build calendar ", e);
        }
        return calendarBuilder;
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

	private void checkTokenAndAuthorize() {
        if (!googleAuthorizationIsNotNull()) {
            calendarBuilder();
        }
        Optional<GoogleToken> refreshedToken = tokenService.getRefreshedToken(GoogleTokenService.TokenType.CALENDAR);
        refreshedToken.ifPresent(googleToken -> credential.setRefreshToken(googleToken.getRefreshToken()));
    }

	@Override
	public void addEvent(String calendarMentor, Long startDate, Client client) throws IOException {
        checkTokenAndAuthorize();
        if (googleAuthorizationIsNotNull()) {
            Event event = newEvent(startDate, client);
            com.google.api.services.calendar.model.Calendar calendar =
                    calendarBuilder.calendars().get(calendarMentor).execute();
            calendarBuilder.events().insert(calendar.getId(), event).execute();
        }
	}

	@Override
	public void update(Long newDate, Long oldDate, String calendarMentor, Client client) throws IOException {
        checkTokenAndAuthorize();
        if (googleAuthorizationIsNotNull()) {
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
	}

	@Override
	public void delete(Long oldDate, String calendarMentor) {
        checkTokenAndAuthorize();
        if (googleAuthorizationIsNotNull()) {
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
