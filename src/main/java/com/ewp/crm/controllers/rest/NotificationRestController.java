package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Notification;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/notification")
public class NotificationRestController {

	private final ClientService clientService;
	private final NotificationService notificationService;

	public NotificationRestController(ClientService clientService, NotificationService notificationService) {
		this.clientService = clientService;
		this.notificationService = notificationService;
	}

	@PostMapping("/sms/clear/{clientId}")
	public ResponseEntity clearClientSmsNotifications(@PathVariable("clientId") long id) {
		User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Client client = clientService.getClientByID(id);
		notificationService.deleteByTypeAndClientAndUserToNotify(Notification.Type.SMS, client, principal);
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@RequestMapping(value = "/markAsRead", method = RequestMethod.POST)
	public ResponseEntity markAsRead(@RequestParam(name = "id") Long id) {
		User userFromSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Client client = clientService.getClientByID(id);
		notificationService.deleteByTypeAndClientAndUserToNotify(Notification.Type.COMMENT,client,userFromSession);
		return ResponseEntity.ok(HttpStatus.OK);
	}
}
