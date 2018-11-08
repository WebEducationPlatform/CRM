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
		// Проверка менеджера на авторизацию в гугл аккаунте, чтоб можно было в будущем назначить звонок
		if (calendarService.googleAuthorizationIsNotNull()){
			User mentor = userService.get(idMentor);
			Client client = clientService.getClientByID(clientId);
			if (!(mentor.getEmail().toLowerCase().contains("@gmail.com")) || mentor.getEmail() == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("У этого ментора неверный формат почты. (Нужен ...@gmail.com)");
			} else {
				try {
					calendarService.getCalendarBuilder().calendars().get(mentor.getEmail()).execute();
				} catch (Exception e){
					return ResponseEntity.badRequest().body("Календарь ментора не привязан к календарю администратора.");
				}
			}
			AssignSkypeCall assignSkypeCallBySkypeLogin = assignSkypeCallService.getAssignSkypeCallByClientId(client.getId());
			//Возможность менеджеру изменить только уведомления и оставить время
			//    чтобы не было ошибки(Текущая дата занята) у одного и того же клиента.
			if (assignSkypeCallBySkypeLogin != null && idMentor.equals(assignSkypeCallBySkypeLogin.getFromAssignSkypeCall().getId())) {
				ZonedDateTime skypeCallDate = Instant.ofEpochMilli(startDate)
						.atZone(ZoneId.of("+00:00"))
						.withZoneSameLocal(ZoneId.of("Europe/Moscow"))
						.withZoneSameInstant(ZoneId.systemDefault());
				if (assignSkypeCallBySkypeLogin.getSkypeCallDate().equals(skypeCallDate)) {
					return ResponseEntity.status(HttpStatus.OK).build();
				}
			}
			if (calendarService.checkFreeDate(startDate, mentor.getEmail())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Текущая дата уже занята, выберите другую.");
			}
			return ResponseEntity.status(HttpStatus.OK).build();
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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
			calendarService.addEvent(mentor.getEmail(), startDate, client);
			AssignSkypeCall clientAssignSkypeCall = new AssignSkypeCall(userFromSession, mentor, client, ZonedDateTime.now(), dateSkypeCall, notificationBeforeOfSkypeCall, selectNetwork);
			assignSkypeCallService.addSkypeCall(clientAssignSkypeCall);
			client.setLiveSkypeCall(true);
			client.addHistory(clientHistoryService.createHistory(userFromSession, client, ClientHistory.Type.SKYPE));
			clientService.update(client);
			logger.info("{} добавил клиенту id:{} звонок по скайпу на {}", userFromSession.getFullName(), client.getId(), dateSkypeCall);
			return ResponseEntity.ok(HttpStatus.OK);
		} catch (Exception e) {
			logger.info("{} не смог добавить клиенту id:{} звокон по скайпу на {}", userFromSession.getFullName(), client.getId(), startDate, e);
			return ResponseEntity.badRequest().body("Произошла ошибка.");
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
		Client client = clientService.get(clientId);
		User mentor = userService.get(mentorId);
		try {
			AssignSkypeCall assignSkypeCall = assignSkypeCallService.getAssignSkypeCallByClientId(client.getId());
			assignSkypeCall.setCreatedTime(ZonedDateTime.now());
			ZonedDateTime dateSkypeCall = Instant.ofEpochMilli(skypeCallDateNew).atZone(ZoneId.of("+00:00")).withZoneSameLocal(ZoneId.of("Europe/Moscow"));
			assignSkypeCall.setSelectNetworkForNotifications(selectNetwork);
			assignSkypeCall.setWhoCreatedTheSkypeCall(userFromSession);
			assignSkypeCall.setTheNotificationWasIsSent(false);
			if (!(Objects.equals(skypeCallDateNew, skypeCallDateOld) && assignSkypeCall.getFromAssignSkypeCall().getId().equals(mentorId))) {
				assignSkypeCall.setSkypeCallDate(dateSkypeCall);
				assignSkypeCall.setNotificationBeforeOfSkypeCall(Instant.ofEpochMilli(skypeCallDateNew).atZone(ZoneId.of("+00:00")).withZoneSameLocal(ZoneId.of("Europe/Moscow")).minusHours(1));
				calendarService.update(skypeCallDateNew, skypeCallDateOld, mentor.getEmail(), client);
				client.addHistory(clientHistoryService.createHistory(userFromSession, client, ClientHistory.Type.SKYPE_UPDATE));
			}
			assignSkypeCallService.update(assignSkypeCall);
			clientService.updateClient(client);
			logger.info("{} изменил клиенту id:{} звонок по скайпу на {}", userFromSession.getFullName(), client.getId(), dateSkypeCall);
			return ResponseEntity.ok(HttpStatus.OK);
		} catch (Exception e) {
			logger.info("{} не смог изменить клиенту id:{} звокон по скайпу на {}", userFromSession.getFullName(), client.getId(), skypeCallDateNew, e);
			return ResponseEntity.badRequest().body("Произошла ошибка.");
		}
	}

	@PostMapping(value = "rest/mentor/deleteEvent")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity deleteEvent(@AuthenticationPrincipal User principal,
									  @RequestParam(name = "clientId") Long clientId,
									  @RequestParam(name = "idMentor") Long mentorId,
									  @RequestParam Long skypeCallDateOld) {
		if (calendarService.googleAuthorizationIsNotNull()){
			User mentor = userService.get(mentorId);
			Client client = clientService.get(clientId);
			client.setLiveSkypeCall(false);
			calendarService.delete(skypeCallDateOld, mentor.getEmail());
			client.addHistory(clientHistoryService.createHistory(principal, client, ClientHistory.Type.SKYPE_DELETE));
			clientService.updateClient(client);
			assignSkypeCallService.deleteByIdSkypeCall(assignSkypeCallService.getAssignSkypeCallByClientId(client.getId()).getId());
			return ResponseEntity.ok(HttpStatus.OK);
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}
}