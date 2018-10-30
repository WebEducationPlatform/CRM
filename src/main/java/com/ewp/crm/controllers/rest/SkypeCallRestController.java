package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.AssignSkypeCall;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.*;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
	@RequestMapping(value = "rest/skype/allMentors", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<User>> getAllMentors(@AuthenticationPrincipal User currentUser) {
		List<User> users = userService.getByRole(roleService.getRoleByName("MENTOR"));
		users.remove(userService.get(currentUser.getId()));
		return ResponseEntity.ok(users);
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	@RequestMapping(value = "rest/skype/checkFreeDate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> checkFreeDate(@RequestParam(name = "idMentor") Long idMentor,
												@RequestParam(name = "startDateOld") Long startDate) {
		User user = userService.get(idMentor);
		if (calendarService.checkFreeDate(startDate, user.getEmail())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	@RequestMapping(value = "rest/skype/getSkypeCallNotificationChecked", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getSkypeCallNotificationChecked(@RequestParam Long clientId) {
		Client client = clientService.getClientByID(clientId);
		AssignSkypeCall clientAssignSkypeCall = assignSkypeCallService.getAssignSkypeCallBySkypeLogin(client.getSkype());
		return ResponseEntity.ok(clientAssignSkypeCall.getSelectNetworkForNotifications());
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	@RequestMapping(value = "rest/mentor/addEvent", method = RequestMethod.POST)
	public ResponseEntity addEventByIdMentor(@AuthenticationPrincipal User principal,
											 @RequestParam(name = "clientId") Long clientId,
											 @RequestParam(name = "idMentor") Long mentorId,
											 @RequestParam(name = "startDateOld") Long startDate) {
		User user = userService.get(mentorId);
		Client client = clientService.get(clientId);
		client.setOwnerCallSkype(mentorId);
		client.setLiveOwnerCallSkype(true);
		client.setDateCallSkype(Instant.ofEpochMilli(startDate).atZone(ZoneId.systemDefault()).withZoneSameLocal(ZoneId.of("Europe/Moscow")).withZoneSameInstant(ZoneId.systemDefault()));
		client.addHistory(clientHistoryService.createHistory(principal, client, ClientHistory.Type.SKYPE));
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
	@RequestMapping(value = "rest/skype/addSkypeCallNotification", method = RequestMethod.POST)
	public ResponseEntity addSkypeCallNotification(@AuthenticationPrincipal User principal,
												   @RequestParam Long clientId,
												   @RequestParam String date,
												   @RequestParam String selectNetwork) {
		Client client = clientService.getClientByID(clientId);
		try {
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm МСК");
			ZonedDateTime remindBeforeSkypeCall = LocalDateTime.parse(date, dateTimeFormatter).atZone(ZoneId.of("Europe/Moscow")).minusHours(1);
			ZonedDateTime zonedDateTimeNow = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Europe/Moscow"));
			if (remindBeforeSkypeCall.isBefore(zonedDateTimeNow) || remindBeforeSkypeCall.isEqual(zonedDateTimeNow)) {
				logger.info("Incorrect date set: {}", date);
				return ResponseEntity.badRequest().body("Уведомление не может быть добавлено, т.к. дата созвона меньше чем через час");
			}
			AssignSkypeCall clientAssignSkypeCall = new AssignSkypeCall(remindBeforeSkypeCall, client.getSkype(), principal, ZonedDateTime.now(), client, selectNetwork);
			client.setDateNotifyCallSkypeNotify(remindBeforeSkypeCall);
			assignSkypeCallService.addSkypeCall(clientAssignSkypeCall);
			logger.info("{} assign skype client id:{} until {}", principal.getFullName(), client.getId(), date);
			return ResponseEntity.ok(HttpStatus.OK);
		} catch (Exception e) {
			logger.info("{}  do not assign skype client id:{} until {}", principal.getFullName(), client.getId(), date, e);
			return ResponseEntity.badRequest().body("Произошла ошибка");
		}
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	@RequestMapping(value = "rest/mentor/updateEvent", method = RequestMethod.POST)
	public ResponseEntity updateEvent(@AuthenticationPrincipal User principal,
									  @RequestParam(name = "clientId") Long clientId,
									  @RequestParam(name = "idMentor") Long mentorId,
									  @RequestParam(name = "startDateNew") Long startDate,
									  @RequestParam(name = "startDateOld") Long startDateOld) {
		User user = userService.get(mentorId);
		Client client = clientService.get(clientId);
		calendarService.update(startDate, startDateOld, user.getEmail(), client.getSkype());
		client.setDateCallSkype(Instant.ofEpochMilli(startDate).atZone(ZoneId.systemDefault()).withZoneSameLocal(ZoneId.of("Europe/Moscow")));
		client.setDateNotifyCallSkypeNotify(Instant.ofEpochMilli(startDate).atZone(ZoneId.systemDefault()).withZoneSameLocal(ZoneId.of("Europe/Moscow")).minusHours(1));
		client.addHistory(clientHistoryService.createHistory(principal, client, ClientHistory.Type.SKYPE_UPDATE));
		clientService.updateClient(client);
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	@RequestMapping(value = "rest/skype/updateSkypeCallNotification", method = RequestMethod.POST)
	public ResponseEntity updateSkypeCallNotification(@RequestParam Long clientId,
													  @RequestParam String selectNetwork) {
		Client client = clientService.getClientByID(clientId);
		AssignSkypeCall clientAssignSkypeCall = assignSkypeCallService.getAssignSkypeCallBySkypeLogin(client.getSkype());
		clientAssignSkypeCall.setSelectNetworkForNotifications(selectNetwork);
		assignSkypeCallService.update(clientAssignSkypeCall);
		return ResponseEntity.badRequest().body("Произошла ошибка");
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	@RequestMapping(value = "rest/mentor/deleteEvent", method = RequestMethod.POST)
	public ResponseEntity deleteEvent(@AuthenticationPrincipal User principal,
									  @RequestParam(name = "clientId") Long clientId,
									  @RequestParam(name = "idMentor") Long mentorId,
									  @RequestParam(name = "startDateOld") Long startDateOld) {
		User user = userService.get(mentorId);
		calendarService.delete(startDateOld, user.getEmail());
		Client client = clientService.get(clientId);
		client.addHistory(clientHistoryService.createHistory(principal, client, ClientHistory.Type.SKYPE_DELETE));
		client.setOwnerCallSkype(mentorId);
		client.setLiveOwnerCallSkype(false);
		client.setDateCallSkype(null);
		client.setDateNotifyCallSkypeNotify(null);
		clientService.updateClient(client);
		assignSkypeCallService.deleteByIdSkypeCall(assignSkypeCallService.getAssignSkypeCallBySkypeLogin(client.getSkype()).getId());
		return ResponseEntity.ok(HttpStatus.OK);
	}
}