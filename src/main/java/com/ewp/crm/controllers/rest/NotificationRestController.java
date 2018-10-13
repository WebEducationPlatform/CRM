package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.Notification;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.ClientHistoryService;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.NotificationService;
import com.ewp.crm.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
	private UserService userService;

	@Autowired
	public NotificationRestController(ClientService clientService,
									  NotificationService notificationService,
									  ClientHistoryService clientHistoryService) {
		this.clientService = clientService;
		this.notificationService = notificationService;
		this.clientHistoryService = clientHistoryService;
	}

	@GetMapping("/sms/error/{clientId}")
	public ResponseEntity getSMSErrorsByClient(@PathVariable("clientId")Long id,
											   @AuthenticationPrincipal User userFromSession) {
		List<Notification> list = notificationService.getByUserToNotifyAndTypeAndClient(userFromSession, Notification.Type.SMS, clientService.get(id));
		if (list == null || list.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(list);
	}

	@PostMapping("/sms/clear/{clientId}")
	public ResponseEntity clearClientSmsNotifications(@PathVariable("clientId") long id,
													  @AuthenticationPrincipal User userFromSession) {
		Client client = clientService.get(id);
		notificationService.deleteByTypeAndClientAndUserToNotify(Notification.Type.SMS, client, userFromSession);
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@PostMapping(value = "/comment/clear/{clientId}")
	public ResponseEntity markAsRead(@PathVariable("clientId") long id,
									 @AuthenticationPrincipal User userFromSession) {
		Client client = clientService.get(id);
		List<Notification> notifications = notificationService.getByUserToNotifyAndTypeAndClient(userFromSession, Notification.Type.POSTPONE, client);
		notificationService.deleteByTypeAndClientAndUserToNotify(Notification.Type.COMMENT, client, userFromSession);
		notificationService.deleteByTypeAndClientAndUserToNotify(Notification.Type.POSTPONE, client, userFromSession);
		notificationService.deleteByTypeAndClientAndUserToNotify(Notification.Type.NEW_USER, client, userFromSession);
		for (Notification notification : notifications) {
			if (notification.getType() == Notification.Type.POSTPONE) {
				ClientHistory clientHistory = clientHistoryService.createHistory(userFromSession, client, ClientHistory.Type.NOTIFICATION);
				clientHistory.setClient(client);
				clientHistoryService.addHistory(clientHistory);
			}
		}
		return ResponseEntity.ok(HttpStatus.OK);
	}
	@PostMapping(value = "/comment/cleanAll")
	public ResponseEntity markAsReadAll(@AuthenticationPrincipal User userFromSession) {
		List<Client> clients = clientService.getAllClients();
		List<Notification> notifications;
		for (Client client : clients) {
			notifications = notificationService.getByUserToNotifyAndTypeAndClient(userFromSession,Notification.Type.POSTPONE,client);
			notificationService.deleteByTypeAndClientAndUserToNotify(Notification.Type.COMMENT, client, userFromSession);
			notificationService.deleteByTypeAndClientAndUserToNotify(Notification.Type.POSTPONE, client, userFromSession);
			notificationService.deleteByTypeAndClientAndUserToNotify(Notification.Type.NEW_USER, client, userFromSession);
			for (Notification notification : notifications) {
				if (notification.getType() == Notification.Type.POSTPONE) {
					ClientHistory clientHistory = clientHistoryService.createHistory(userFromSession, client, ClientHistory.Type.NOTIFICATION);
					clientHistory.setClient(client);
					clientHistoryService.addHistory(clientHistory);
				}
			}
		}
		return ResponseEntity.ok(HttpStatus.OK);
	}
	@PostMapping(value = "/comment/cleanAllNewUserNotify")
	public ResponseEntity markAsReadAllNewUserNotify(@AuthenticationPrincipal User userFromSession) {
	if (userFromSession.isNewClienNotifyIsEnabled())
	    userFromSession.setNewClienNotifyIsEnabled(false);
	else userFromSession.setNewClienNotifyIsEnabled(true);
        userService.update(userFromSession);
		List<Client> clients = clientService.getAllClients();
		for (Client client : clients) {
			notificationService.deleteByTypeAndClientAndUserToNotify(Notification.Type.NEW_USER, client, userFromSession);
		}
		return ResponseEntity.ok(HttpStatus.OK);
	}

}
