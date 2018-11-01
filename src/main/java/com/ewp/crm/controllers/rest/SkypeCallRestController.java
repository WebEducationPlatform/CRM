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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

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
	public SkypeCallRestController(AssignSkypeCallService assignSkypeCallService,
								   ClientHistoryService clientHistoryService,
								   ClientService clientService,
								   UserService userService,
								   RoleService roleService,
								   GoogleCalendarService calendarService) {
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
	public ResponseEntity<Object> checkFreeDate(@RequestParam Long clientId,
												@RequestParam(name = "idMentor") Long idMentor,
												@RequestParam(name = "startDateOld") Long startDate) {
		User user = userService.get(idMentor);
		Client client = clientService.getClientByID(clientId);
		AssignSkypeCall assignSkypeCallBySkypeLogin = assignSkypeCallService.getAssignSkypeCallBySkypeLogin(client.getSkype());
		if (assignSkypeCallBySkypeLogin != null) {
			ZonedDateTime dateSkypeCall = Instant.ofEpochMilli(startDate)
					.atZone(ZoneId.of("+00:00"))
					.withZoneSameLocal(ZoneId.of("Europe/Moscow"))
					.withZoneSameInstant(ZoneId.systemDefault());
			if (assignSkypeCallBySkypeLogin.getDateSkypeCall() == dateSkypeCall) {
				return ResponseEntity.status(HttpStatus.OK).build();
			}
		}
		if (calendarService.checkFreeDate(startDate, user.getEmail())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	@RequestMapping(value = "rest/skype/addSkypeCallAndNotification", method = RequestMethod.POST)
	public ResponseEntity addSkypeCallAndNotification(@AuthenticationPrincipal User userFromSession,
													  @RequestParam Long clientId,
													  @RequestParam(name = "idMentor") Long mentorId,
													  @RequestParam(name = "startDateOld") Long startDate,
													  @RequestParam String date,
													  @RequestParam String selectNetwork) {
		Client client = clientService.getClientByID(clientId);
		User user = userService.get(mentorId);
		try {
			calendarService.addEvent(user.getEmail(), startDate, client.getSkype());
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm МСК");
			ZonedDateTime dateSkypeCall = LocalDateTime.parse(date, dateTimeFormatter).atZone(ZoneId.of("Europe/Moscow"));
			ZonedDateTime notificationBeforeOfSkypeCall = LocalDateTime.parse(date, dateTimeFormatter).atZone(ZoneId.of("Europe/Moscow")).minusHours(1);
			AssignSkypeCall clientAssignSkypeCall = new AssignSkypeCall(client.getSkype(), userFromSession, client, ZonedDateTime.now(), dateSkypeCall, notificationBeforeOfSkypeCall, selectNetwork);
			assignSkypeCallService.addSkypeCall(clientAssignSkypeCall);
			client.setLiveSkypeCall(true);
			client.addHistory(clientHistoryService.createHistory(userFromSession, client, ClientHistory.Type.SKYPE));
			clientService.update(client);
			logger.info("{} assign skype client id:{} until {}", userFromSession.getFullName(), client.getId(), date);
			return ResponseEntity.ok(HttpStatus.OK);
		} catch (Exception e) {
			logger.info("{}  do not assign skype client id:{} until {}", userFromSession.getFullName(), client.getId(), date, e);
			return ResponseEntity.badRequest().body("Произошла ошибка");
		}
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	@RequestMapping(value = "rest/mentor/updateEvent", method = RequestMethod.POST)
	public ResponseEntity updateEvent(@AuthenticationPrincipal User principal,
									  @RequestParam(name = "clientId") Long clientId,
									  @RequestParam(name = "idMentor") Long mentorId,
									  @RequestParam(name = "startDateNew") Long startDate,
									  @RequestParam(name = "startDateOld") Long startDateOld,
									  @RequestParam String selectNetwork) {
		User user = userService.get(mentorId);
		Client client = clientService.get(clientId);
		AssignSkypeCall assignSkypeCall = assignSkypeCallService.getAssignSkypeCallBySkypeLogin(client.getSkype());
		assignSkypeCall.setCreatedTime(ZonedDateTime.now());
		if (!Objects.equals(startDate, startDateOld)) {
			calendarService.update(startDate, startDateOld, user.getEmail(), client.getSkype());
			assignSkypeCall.setDateSkypeCall(Instant.ofEpochMilli(startDate).atZone(ZoneId.of("+00:00")).withZoneSameLocal(ZoneId.of("Europe/Moscow")));
			assignSkypeCall.setNotificationBeforeOfSkypeCall(Instant.ofEpochMilli(startDate).atZone(ZoneId.of("+00:00")).withZoneSameLocal(ZoneId.of("Europe/Moscow")).minusHours(1));
		}
		assignSkypeCall.setSelectNetworkForNotifications(selectNetwork);
		client.addHistory(clientHistoryService.createHistory(principal, client, ClientHistory.Type.SKYPE_UPDATE));
		assignSkypeCallService.update(assignSkypeCall);
		clientService.updateClient(client);
		return ResponseEntity.ok(HttpStatus.OK);
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
		assignSkypeCallService.deleteByIdSkypeCall(assignSkypeCallService.getAssignSkypeCallBySkypeLogin(client.getSkype()).getId());
		client.setLiveSkypeCall(false);
		client.addHistory(clientHistoryService.createHistory(principal, client, ClientHistory.Type.SKYPE_DELETE));
		clientService.updateClient(client);
		return ResponseEntity.ok(HttpStatus.OK);
	}
}