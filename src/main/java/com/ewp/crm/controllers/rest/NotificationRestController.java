package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.Notification;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.ClientHistoryService;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
@RequestMapping("/user/notification")
public class NotificationRestController {

	private final ClientService clientService;
	private final NotificationService notificationService;
	private final ClientHistoryService clientHistoryService;

	@Autowired
	public NotificationRestController(ClientService clientService, NotificationService notificationService, ClientHistoryService clientHistoryService) {
		this.clientService = clientService;
		this.notificationService = notificationService;
		this.clientHistoryService = clientHistoryService;
	}

	@PostMapping("/sms/clear/{clientId}")
	public ResponseEntity clearClientSmsNotifications(@PathVariable("clientId") long id) {
		User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Client client = clientService.get(id);
		notificationService.deleteByTypeAndClientAndUserToNotify(Notification.Type.SMS, client, principal);
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@RequestMapping(value = "/comment/clear/{clientId}", method = RequestMethod.POST)
	public ResponseEntity markAsRead(@PathVariable("clientId") long id) {
		User userFromSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Client client = clientService.get(id);
		List<Notification> notifications = notificationService.getByUserToNotifyAndTypeAndClient(userFromSession, Notification.Type.POSTPONE, client);
		notificationService.deleteByTypeAndClientAndUserToNotify(Notification.Type.COMMENT, client, userFromSession);
		notificationService.deleteByTypeAndClientAndUserToNotify(Notification.Type.POSTPONE, client, userFromSession);
		notificationService.deleteByTypeAndClientAndUserToNotify(Notification.Type.NEW_USER, client, userFromSession);
		notificationService.deleteByTypeAndClientAndUserToNotify(Notification.Type.ASSIGN_SKYPE, client, userFromSession);
		for (Notification notification : notifications) {
			if (notification.getType() == Notification.Type.POSTPONE) {
				ClientHistory clientHistory = clientHistoryService.createHistory(userFromSession, client, ClientHistory.Type.NOTIFICATION);
				clientHistory.setClient(client);
				clientHistoryService.addHistory(clientHistory);
			}
		}
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@GetMapping("/sms/error/{clientId}")
	public ResponseEntity getSMSErrorsByClient(@PathVariable("clientId")Long id) {
		User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Notification> list = notificationService.getByUserToNotifyAndTypeAndClient(principal, Notification.Type.SMS, clientService.get(id));
		if (list == null || list.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(list);
	}
}
