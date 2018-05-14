package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.Status;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.ClientHistoryService;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.StatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class StatusRestController {

	private static Logger logger = LoggerFactory.getLogger(StatusRestController.class);

	private final StatusService statusService;
	private final ClientService clientService;
	private final ClientHistoryService clientHistoryService;

	@Autowired
	public StatusRestController(StatusService statusService, ClientService clientService, ClientHistoryService clientHistoryService) {
		this.statusService = statusService;
		this.clientService = clientService;
		this.clientHistoryService = clientHistoryService;
	}

	@RequestMapping(value = "/rest/status/add", method = RequestMethod.POST)
	public ResponseEntity addNewStatus(@RequestParam(name = "statusName") String statusName) {
		statusService.add(new Status(statusName));
		User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		logger.info("{} has added status with name: {}", currentAdmin.getFullName(), statusName);
		return ResponseEntity.ok("Успешно добавлено");
	}

	@RequestMapping(value = "/admin/rest/status/edit", method = RequestMethod.POST)
	public ResponseEntity editStatus(@RequestParam(name = "statusName") String statusName, @RequestParam(name = "oldStatusId") Long oldStatusId) {
		User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Status status = statusService.get(oldStatusId);
		status.setName(statusName);
		statusService.update(status);
		logger.info("{} has updated status {}", currentAdmin.getFullName(), statusName);
		return ResponseEntity.ok().build();
	}

	@RequestMapping(value = "/rest/status/client/change", method = RequestMethod.POST)
	public ResponseEntity changeClientStatus(@RequestParam(name = "statusId") Long statusId,
	                                         @RequestParam(name = "clientId") Long clientId) {
		Client currentClient = clientService.getClientByID(clientId);
		if (currentClient.getStatus().getId().equals(statusId)) {
			return ResponseEntity.badRequest().build();
		}
		User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		currentClient.setStatus(statusService.get(statusId));
		ClientHistory clientHistory = new ClientHistory(ClientHistory.Type.STATUS, principal);
		clientHistoryService.generateValidHistory(clientHistory, currentClient);
		currentClient.addHistory(clientHistory);
		clientService.updateClient(currentClient);
		logger.info("{} has changed status of client with id: {} to status id: {}", principal.getFullName(), clientId, statusId);
		return ResponseEntity.ok().build();
	}

	@RequestMapping(value = "/admin/rest/status/delete", method = RequestMethod.POST)
	public ResponseEntity deleteStatus(@RequestParam(name = "deleteId") Long deleteId) {
		User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		statusService.delete(deleteId);
		logger.info("{} has  deleted status  with id {}", currentAdmin.getFullName(), deleteId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/admin/rest/status/client/delete")
	public ResponseEntity deleteClientStatus(@RequestParam("clientId") long clientId) {
		User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Client client = clientService.getClientByID(clientId);
		if (client == null) {
			logger.error("Can`t delete client status, client with id = {} not found", clientId);
			return ResponseEntity.notFound().build();
		}
		Status status = client.getStatus();
		client.setStatus(statusService.get("default"));
		ClientHistory clientHistory = new ClientHistory(ClientHistory.Type.STATUS, principal);
		clientHistoryService.generateValidHistory(clientHistory, client);
		client.addHistory(clientHistory);
		clientService.updateClient(client);
		logger.info("{} delete client with id = {} in status {}", principal.getFullName(), client.getId(), status.getName());
		return ResponseEntity.ok().build();
	}
}
