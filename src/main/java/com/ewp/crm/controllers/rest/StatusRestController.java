package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.*;
import com.ewp.crm.service.interfaces.ClientHistoryService;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.NotificationService;
import com.ewp.crm.service.interfaces.StatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StatusRestController {

	private static Logger logger = LoggerFactory.getLogger(StatusRestController.class);

	private final StatusService statusService;
	private final ClientService clientService;
	private final ClientHistoryService clientHistoryService;
	private final NotificationService notificationService;

	@Autowired
	public StatusRestController(StatusService statusService,
								ClientService clientService,
								ClientHistoryService clientHistoryService,
								NotificationService notificationService) {
		this.statusService = statusService;
		this.clientService = clientService;
		this.clientHistoryService = clientHistoryService;
		this.notificationService = notificationService;
	}

	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	@GetMapping(value = "/rest/status/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Client>> getStatusByID(@PathVariable Long id) {
		Status status = statusService.get(id);
		return ResponseEntity.ok(clientService.findAllByStatus(status));
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
	@PostMapping(value = "/rest/status/add")
	public ResponseEntity addNewStatus(@RequestParam(name = "statusName") String statusName,
									   @AuthenticationPrincipal User currentAdmin) {
		statusService.add(new Status(statusName));
		logger.info("{} has added status with name: {}", currentAdmin.getFullName(), statusName);
		return ResponseEntity.ok("Успешно добавлено");
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	@PostMapping(value = "/rest/status/client/change")
	public ResponseEntity changeClientStatus(@RequestParam(name = "statusId") Long statusId,
	                                         @RequestParam(name = "clientId") Long clientId,
											 @AuthenticationPrincipal User userFromSession) {
		Client currentClient = clientService.get(clientId);
		if (currentClient.getStatus().getId().equals(statusId)) {
			return ResponseEntity.badRequest().body("Клиент уже находится на данном статусе");
		}
		currentClient.setStatus(statusService.get(statusId));
		currentClient.addHistory(clientHistoryService.createHistory(userFromSession, currentClient, ClientHistory.Type.STATUS));
		clientService.updateClient(currentClient);
		notificationService.deleteNotificationsByClient(currentClient);
		logger.info("{} has changed status of client with id: {} to status id: {}", userFromSession.getFullName(), clientId, statusId);
		return ResponseEntity.ok().build();
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	@PostMapping(value = "/rest/status/client/delete")
	public ResponseEntity deleteClientStatus(@RequestParam("clientId") long clientId,
											 @AuthenticationPrincipal User userFromSession) {
		Client client = clientService.get(clientId);
		if (client == null) {
			logger.error("Can`t delete client status, client with id = {} not found", clientId);
			return ResponseEntity.notFound().build();
		}
		Status status = client.getStatus();
		client.setStatus(statusService.get("deleted"));
		client.addHistory(clientHistoryService.createHistory(userFromSession, client, ClientHistory.Type.STATUS));
		clientService.updateClient(client);
		notificationService.deleteNotificationsByClient(client);
		logger.info("{} delete client with id = {} in status {}", userFromSession.getFullName(), client.getId(), status.getName());
		return ResponseEntity.ok().build();
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping(value = "/rest/status/position/change")
	public ResponseEntity changePositionOfTwoStatuses(@RequestParam("sourceId") long sourceId,
													  @RequestParam("destinationId") long destinationId) {
		Status sourceStatus = statusService.get(sourceId);
		Status destinationStatus = statusService.get(destinationId);
		if (sourceStatus == null || destinationStatus == null) {
			return ResponseEntity.notFound().build();
		}
		Long tempPosition = sourceStatus.getPosition();
		sourceStatus.setPosition(destinationStatus.getPosition());
		destinationStatus.setPosition(tempPosition);
		statusService.update(sourceStatus);
		return ResponseEntity.ok().build();
	}
}
