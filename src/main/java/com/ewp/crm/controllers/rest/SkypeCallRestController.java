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
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

	@GetMapping(value = "rest/skype/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity<AssignSkypeCall> getAssignSkypeCallByClientId(@PathVariable Long clientId) {
		return ResponseEntity.ok(assignSkypeCallService.getAssignSkypeCallByClientId(clientId));
	}

	@GetMapping(value = "rest/skype/allMentors", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity<List<User>> getAllMentors() {
		List<User> users = userService.getByRole(roleService.getRoleByName("MENTOR"));
		return ResponseEntity.ok(users);
	}

	@GetMapping(value = "rest/skype/checkFreeDate", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity<Object> checkFreeDate(@RequestParam Long clientId,
												@RequestParam(name = "idMentor") Long idMentor,
												@RequestParam Long startDate) {
		User user = userService.get(idMentor);
		Client client = clientService.getClientByID(clientId);
		AssignSkypeCall assignSkypeCallBySkypeLogin = assignSkypeCallService.getAssignSkypeCallByClientId(client.getId());
		if (assignSkypeCallBySkypeLogin != null) {
			ZonedDateTime skypeCallDate = Instant.ofEpochMilli(startDate)
					.atZone(ZoneId.of("+00:00"))
					.withZoneSameLocal(ZoneId.of("Europe/Moscow"))
					.withZoneSameInstant(ZoneId.systemDefault());
			if (assignSkypeCallBySkypeLogin.getSkypeCallDate().equals(skypeCallDate)) {
				return ResponseEntity.status(HttpStatus.OK).build();
			}
		}
		if (calendarService.checkFreeDate(startDate, user.getEmail())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PostMapping(value = "rest/skype/addSkypeCallAndNotification")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity addSkypeCallAndNotification(@AuthenticationPrincipal User userFromSession,
													  @RequestParam(name = "idMentor") Long mentorId,
													  @RequestParam Long startDate,
													  @RequestParam Long clientId,
													  @RequestParam String selectNetwork) {
		Client client = clientService.getClientByID(clientId);
		User mentor = userService.get(mentorId);
		ZonedDateTime dateSkypeCall = Instant.ofEpochMilli(startDate).atZone(ZoneId.of("+00:00")).withZoneSameLocal(ZoneId.of("Europe/Moscow"));
		ZonedDateTime notificationBeforeOfSkypeCall = Instant.ofEpochMilli(startDate).atZone(ZoneId.of("+00:00")).withZoneSameLocal(ZoneId.of("Europe/Moscow")).minusHours(1);
		try {
			calendarService.addEvent(mentor.getEmail(), startDate, client.getSkype());
			AssignSkypeCall clientAssignSkypeCall = new AssignSkypeCall(userFromSession, mentor, client, ZonedDateTime.now(), dateSkypeCall, notificationBeforeOfSkypeCall, selectNetwork);
			assignSkypeCallService.addSkypeCall(clientAssignSkypeCall);
			client.setLiveSkypeCall(true);
			client.addHistory(clientHistoryService.createHistory(userFromSession, client, ClientHistory.Type.SKYPE));
			clientService.update(client);
			logger.info("{} assign skype client id:{} until {}", userFromSession.getFullName(), client.getId(), dateSkypeCall);
			return ResponseEntity.ok(HttpStatus.OK);
		} catch (Exception e) {
			logger.info("{}  do not assign skype client id:{} until {}", userFromSession.getFullName(), client.getId(), startDate, e);
			return ResponseEntity.badRequest().body("Произошла ошибка");
		}
	}

	@PostMapping(value = "rest/mentor/updateEvent")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity updateEvent(@AuthenticationPrincipal User userFromSession,
									  @RequestParam(name = "clientId") Long clientId,
									  @RequestParam(name = "idMentor") Long mentorId,
									  @RequestParam Long skypeCallDateNew,
									  @RequestParam Long skypeCallDateOld,
									  @RequestParam String selectNetwork) {
		User user = userService.get(mentorId);
		Client client = clientService.get(clientId);
		AssignSkypeCall assignSkypeCall = assignSkypeCallService.getAssignSkypeCallByClientId(client.getId());
		assignSkypeCall.setCreatedTime(ZonedDateTime.now());
		if (!Objects.equals(skypeCallDateNew, skypeCallDateOld)) {
			calendarService.update(skypeCallDateNew, skypeCallDateOld, user.getEmail(), client.getSkype());
			assignSkypeCall.setSkypeCallDate(Instant.ofEpochMilli(skypeCallDateNew).atZone(ZoneId.of("+00:00")).withZoneSameLocal(ZoneId.of("Europe/Moscow")));
			assignSkypeCall.setNotificationBeforeOfSkypeCall(Instant.ofEpochMilli(skypeCallDateNew).atZone(ZoneId.of("+00:00")).withZoneSameLocal(ZoneId.of("Europe/Moscow")).minusHours(1));
		}
		assignSkypeCall.setSelectNetworkForNotifications(selectNetwork);
		assignSkypeCall.setWhoCreatedTheSkypeCall(userFromSession);
		client.addHistory(clientHistoryService.createHistory(userFromSession, client, ClientHistory.Type.SKYPE_UPDATE));
		assignSkypeCallService.update(assignSkypeCall);
		clientService.updateClient(client);
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@PostMapping(value = "rest/mentor/deleteEvent")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity deleteEvent(@AuthenticationPrincipal User principal,
									  @RequestParam(name = "clientId") Long clientId,
									  @RequestParam(name = "idMentor") Long mentorId,
									  @RequestParam Long skypeCallDateOld) {
		User user = userService.get(mentorId);
		calendarService.delete(skypeCallDateOld, user.getEmail());
		Client client = clientService.get(clientId);
		client.setLiveSkypeCall(false);
		client.addHistory(clientHistoryService.createHistory(principal, client, ClientHistory.Type.SKYPE_DELETE));
		clientService.updateClient(client);
		assignSkypeCallService.deleteByIdSkypeCall(assignSkypeCallService.getAssignSkypeCallByClientId(client.getId()).getId());
		return ResponseEntity.ok(HttpStatus.OK);
	}
}