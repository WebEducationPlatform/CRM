package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.*;
import com.ewp.crm.service.interfaces.*;
import com.google.api.services.calendar.Calendar;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
public class SkypeCallRestController {

	private static Logger logger = LoggerFactory.getLogger(ClientRestController.class);

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
	public ResponseEntity assignSkypeCall(@RequestParam Long clientId, @RequestParam String date, @RequestParam String selectNetwork) {
		User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Client client = clientService.getClientByID(clientId);
		try {
			DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd.MM.YYYY HH:mm МСК");
			LocalDateTime dateOfSkypeCall = LocalDateTime.parse(date, dateTimeFormatter);
			LocalDateTime remindBeforeSkypeCall = LocalDateTime.parse(date, dateTimeFormatter).minusHours(1);
			if (dateOfSkypeCall.isBefore(LocalDateTime.now()) || dateOfSkypeCall.isEqual(LocalDateTime.now())) {
				logger.info("Incorrect date set: {}", date);
				return ResponseEntity.badRequest().body("Дата должна быть позже текущей даты");
			}
			AssignSkypeCall clientAssignSkypeCall = new AssignSkypeCall();
			clientAssignSkypeCall.setRemindBeforeOfSkypeCall(remindBeforeSkypeCall.toDate());
			clientAssignSkypeCall.setLogin(client.getSkype());
			clientAssignSkypeCall.setFromAssignSkypeCall(principal);
			clientAssignSkypeCall.setCreatedTime(new Date());
			clientAssignSkypeCall.setToAssignSkypeCall(client);
			clientAssignSkypeCall.setSelectNetworkForNotifications(selectNetwork);
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
	public ResponseEntity<List<User>> getAllMentors() {
		User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<User> users = userService.getByRole(roleService.getByRoleName("MENTOR"));
		users.remove(userService.get(currentUser.getId()));
		return ResponseEntity.ok(users);
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	@RequestMapping(value = "rest/skype/checkFreeDate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Long> checkFreeDate(@RequestParam(name = "idMentor") Long idMentor, @RequestParam(name = "startDateOld") Date startDate) {
		User user = userService.get(idMentor);
		if (calendarService.checkDate(startDate, user.getEmail())) {
			return ResponseEntity.ok(0L);
		}
		return ResponseEntity.ok(1L);
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	@RequestMapping(value = "rest/mentor/addEvent", method = RequestMethod.POST)
	public ResponseEntity addEventByIdMentor(@RequestParam(name = "clientId") Long clientId, @RequestParam(name = "idMentor") Long idMentor, @RequestParam(name = "startDateOld") Date startDate) {
		User user = userService.get(idMentor);
		Client client = clientService.get(clientId);
		client.setOwnerCallSkype(user.getId());
		client.setDateCallSkype(startDate);
		clientService.updateClient(client);
        try {
            calendarService.addEvent(user.getEmail(), startDate);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(HttpStatus.OK);
	}

    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
    @RequestMapping(value = "rest/mentor/updateEvent", method = RequestMethod.POST)
    public ResponseEntity updateEvent(@RequestParam(name = "clientId") Long clientId, @RequestParam(name = "idMentor") Long idMentor, @RequestParam(name = "startDateNew") Date startDate, @RequestParam(name = "startDateOld") Date startDateOld) {
        User user = userService.get(idMentor);
		calendarService.update(startDate, startDateOld, user.getEmail());
		Client client = clientService.get(clientId);
		client.setDateCallSkype(startDate);
		clientService.updateClient(client);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}