package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.AssignSkypeCall;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.*;
import org.apache.tomcat.jni.Local;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class SkypeCallRestController {
	private static Logger logger = LoggerFactory.getLogger(SkypeCallRestController.class);

	private final AssignSkypeCallService assignSkypeCallService;
	private final ClientHistoryService clientHistoryService;
	private final ClientService clientService;
	private final UserService userService;
	private final RoleService roleService;
	private final GoogleCalendarService calendarService;

	@Autowired
	public SkypeCallRestController(AssignSkypeCallService assignSkypeCallService, ClientHistoryService clientHistoryService, ClientService clientService, UserService userService, RoleService roleService, GoogleCalendarService calendarService) {
		this.assignSkypeCallService = assignSkypeCallService;
		this.clientHistoryService = clientHistoryService;
		this.clientService = clientService;
		this.userService = userService;
		this.roleService = roleService;
		this.calendarService = calendarService;
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	@RequestMapping(value = "rest/skype/assignSkype", method = RequestMethod.POST)
	public ResponseEntity assignSkypeCall(@AuthenticationPrincipal User principal, @RequestParam Long clientId, @RequestParam String date, @RequestParam String selectNetwork) {
		Client client = clientService.getClientByID(clientId);
		try {
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm МСК");
			ZonedDateTime dateOfSkypeCall = LocalDateTime.parse(date, dateTimeFormatter).atZone(ZoneId.of("Europe/Moscow")).withZoneSameInstant(ZoneId.systemDefault());
			ZonedDateTime remindBeforeSkypeCall = LocalDateTime.parse(date, dateTimeFormatter).minusHours(1).atZone(ZoneId.of("Europe/Moscow")).withZoneSameInstant(ZoneId.systemDefault());
			ZonedDateTime zonedDateTimeNow = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Europe/Moscow")).withZoneSameInstant(ZoneId.systemDefault());
			if (dateOfSkypeCall.isBefore(zonedDateTimeNow) || dateOfSkypeCall.isEqual(zonedDateTimeNow)) {
				logger.info("Incorrect date set: {}", date);
				return ResponseEntity.badRequest().body("Дата должна быть позже текущей даты");
			}
			AssignSkypeCall clientAssignSkypeCall = new AssignSkypeCall(remindBeforeSkypeCall, client.getSkype(), principal, ZonedDateTime.now(), client, selectNetwork);
			client.setDateNotifyCallSkypeNotify(dateOfSkypeCall);
			client.addHistory(clientHistoryService.createHistory(principal, client, ClientHistory.Type.SKYPE));
			assignSkypeCallService.addSkypeCall(clientAssignSkypeCall);
			logger.info("{} assign skype client id:{} until {}", principal.getFullName(), client.getId(), date);
			return ResponseEntity.ok(HttpStatus.OK);
		} catch (Exception e) {
			logger.info("{}  do not assign skype client id:{} until {}", principal.getFullName(), client.getId(), date ,e);
			return ResponseEntity.badRequest().body("Произошла ошибка");
		}
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	@RequestMapping(value = "rest/skype/allMentors", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<User>> getAllMentors(@AuthenticationPrincipal User currentUser) {
		List<User> users = userService.getByRole(roleService.getRoleByName("MENTOR"));
		users.remove(userService.get(currentUser.getId()));
		return ResponseEntity.ok(users);
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	@RequestMapping(value = "rest/skype/checkFreeDate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> checkFreeDate(@RequestParam(name = "idMentor") Long idMentor, @RequestParam(name = "startDateOld") Long startDate) {
		User user = userService.get(idMentor);
		if (calendarService.checkFreeDate(startDate, user.getEmail())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	@RequestMapping(value = "rest/mentor/addEvent", method = RequestMethod.POST)
	public ResponseEntity addEventByIdMentor(@RequestParam(name = "clientId") Long clientId, @RequestParam(name = "idMentor") Long mentorId, @RequestParam(name = "startDateOld") Long startDate) {
		User user = userService.get(mentorId);
		Client client = clientService.get(clientId);
		client.setOwnerCallSkype(user.getId());
		client.setDateCallSkype(LocalDateTime.ofInstant(Instant.ofEpochMilli(startDate), ZoneId.of("+00:00")));
		clientService.updateClient(client);
        try {
            calendarService.addEvent(user.getEmail(), startDate, client.getSkype());
        } catch (IOException e) {
			logger.error("Error to send message ", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Message not sent");
        }
        return ResponseEntity.ok(HttpStatus.OK);
	}

    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
    @RequestMapping(value = "rest/mentor/updateEvent", method = RequestMethod.POST)
    public ResponseEntity updateEvent(@RequestParam(name = "clientId") Long clientId, @RequestParam(name = "idMentor") Long mentorId, @RequestParam(name = "startDateNew") Long startDate, @RequestParam(name = "startDateOld") Long startDateOld) {
        User user = userService.get(mentorId);
		Client client = clientService.get(clientId);
		calendarService.update(startDate, startDateOld, user.getEmail(), client.getSkype());
		client.setDateCallSkype(Instant.ofEpochMilli(startDate).atZone(ZoneId.systemDefault()).toLocalDateTime());
		clientService.updateClient(client);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}