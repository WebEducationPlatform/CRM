package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.AssignSkypeCall;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.ClientHistoryService;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.AssignSkypeCallService;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class SkypeCallRestController {

	private static Logger logger = LoggerFactory.getLogger(ClientRestController.class);

	private final AssignSkypeCallService assignSkypeCallService;
	private final ClientHistoryService clientHistoryService;
	private final ClientService clientService;

	@Autowired
	public SkypeCallRestController(AssignSkypeCallService assignSkypeCallService, ClientHistoryService clientHistoryService, ClientService clientService) {
		this.assignSkypeCallService = assignSkypeCallService;
		this.clientHistoryService = clientHistoryService;
		this.clientService = clientService;
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	@RequestMapping(value = "rest/skype/assignSkype", method = RequestMethod.POST)
	public ResponseEntity assignSkypeCall(@RequestParam Long clientId, @RequestParam String date, @RequestParam String selectNetwork) {
		User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Client client = clientService.getClientByID(clientId);
		try {
			DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd.MM.YYYY HH:mm");
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
//			clientService.updateClient(client);
			logger.info("{} assign skype client id:{} until {}", principal.getFullName(), client.getId(), date);
			return ResponseEntity.ok(HttpStatus.OK);
		} catch (Exception e) {
			logger.info("{}  do not assign skype client id:{} until {}", principal.getFullName(), client.getId(), date ,e);
			return ResponseEntity.badRequest().body("Произошла ошибка");
		}
	}
}
