package com.ewp.crm.service.google;

import com.ewp.crm.configs.GoogleAPIConfigImpl;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.GoogleToken;
import com.ewp.crm.service.interfaces.GoogleCalendarService;
import com.ewp.crm.service.interfaces.GoogleTokenService;
import com.google.api.client.util.DateTime;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class GoogleCalendarServiceImpl implements GoogleCalendarService {

    private static final String GOOGLE_API_URL = "https://www.googleapis.com/calendar/v3";
	private static Logger logger = LoggerFactory.getLogger(GoogleCalendarServiceImpl.class);

	private final GoogleTokenService tokenService;
	private final String calendarName;
	private final String eventName;

	@Autowired
	public GoogleCalendarServiceImpl(GoogleTokenService tokenService,
                                     GoogleAPIConfigImpl config) {
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
        Optional<GoogleToken> tokenOpt = tokenService.getRefreshedToken(GoogleToken.TokenType.CALENDAR);
        if (tokenOpt.isPresent()) {
            String token = tokenOpt.get().getAccessToken();
            String uri = GOOGLE_API_URL + "/users/me/calendarList"
                    + "?access_token=" + token;
            try {
                HttpGet httpGet = new HttpGet(uri);
                httpGet.addHeader("Content-Type", "application/json; charset=UTF-8");
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

        String description = String.format("%s %s", client.getLastName(), client.getName());

        root.addProperty("summary", eventName);
        root.addProperty("description", description);

	    JsonArray attendees = new JsonArray();
	    JsonObject clientAttendee = new JsonObject();
	    Optional<String> optionalEmail = client.getEmail();
	    if (optionalEmail.isPresent()) {
            clientAttendee.addProperty("email", optionalEmail.get());
        } else {
            logger.warn("email not found", client.getId());
        }
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
            Optional<GoogleToken> tokenOpt = tokenService.getRefreshedToken(GoogleToken.TokenType.CALENDAR);
            if (tokenOpt.isPresent()) {
                String token = tokenOpt.get().getAccessToken();
                String uri = GOOGLE_API_URL + String.format("/calendars/%s/events", calendarId.get())
                        + "?access_token=" + token +
                        "&sendUpdates=all";
                HttpPost request = new HttpPost(uri);
                request.addHeader("Content-Type", "application/json; charset=UTF-8");
                Optional<String> eventJson = createCalendarEventRequestBody(eventStart, client);
                if (eventJson.isPresent()) {
                    HttpEntity entity = new StringEntity(eventJson.get(), "UTF-8");
                    request.setEntity(entity);
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
            Optional<GoogleToken> tokenOpt = tokenService.getRefreshedToken(GoogleToken.TokenType.CALENDAR);
            if (tokenOpt.isPresent()) {
                String token = tokenOpt.get().getAccessToken();
                String uri = GOOGLE_API_URL + String.format("/calendars/%s/events/%s", calendarId.get(), eventId)
                        + "?access_token=" + token +
                        "&sendUpdates=all";
                HttpPut request = new HttpPut(uri);
                request.addHeader("Content-Type", "application/json; charset=UTF-8");
                Optional<String> eventJson = createCalendarEventRequestBody(eventStart, client);
                if (eventJson.isPresent()) {
                    HttpEntity entity = new StringEntity(eventJson.get(), "UTF-8");
                    request.setEntity(entity);
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
            Optional<GoogleToken> tokenOpt = tokenService.getRefreshedToken(GoogleToken.TokenType.CALENDAR);
            if (tokenOpt.isPresent()) {
                String token = tokenOpt.get().getAccessToken();
                String uri = GOOGLE_API_URL + String.format("/calendars/%s/events/%s", calendarId.get(), eventId)
                        + "?access_token=" + token +
                        "&sendUpdates=all";
                HttpDelete request = new HttpDelete(uri);
                request.addHeader("Content-Type", "application/json; charset=UTF-8");
                HttpClient httpClient = getHttpClient();
                try {
                    HttpResponse response = httpClient.execute(request);
                } catch (IOException e) {
                    logger.error("Can't send delete request to calendar: " + uri, e);
                }
            }
        }
    }
}
