package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.*;
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

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest/status")
public class StatusRestController {

	private static Logger logger = LoggerFactory.getLogger(StatusRestController.class);

	private final StatusService statusService;
	private final ClientService clientService;
	private final ClientHistoryService clientHistoryService;
	private final NotificationService notificationService;
	private final StudentService studentService;
	private final StudentStatusService studentStatusService;

	@Autowired
	public StatusRestController(StatusService statusService, ClientService clientService, ClientHistoryService clientHistoryService, NotificationService notificationService, StudentService studentService, StudentStatusService studentStatusService) {
		this.statusService = statusService;
		this.clientService = clientService;
		this.clientHistoryService = clientHistoryService;
		this.notificationService = notificationService;
		this.studentService = studentService;
		this.studentStatusService = studentStatusService;
	}

	@GetMapping
	public ResponseEntity<List<Status>> getAllClientStatuses() {
		return ResponseEntity.ok(statusService.getAll());
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	public ResponseEntity<List<Client>> getStatusByID(@PathVariable Long id) {
		return statusService.get(id).map(s -> ResponseEntity.ok(clientService.getAllClientsByStatus(s))).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@PostMapping(value = "/add")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
	public ResponseEntity addNewStatus(@RequestParam(name = "statusName") String statusName,
									   @AuthenticationPrincipal User currentAdmin) {
		final Status status = new Status(statusName);
		status.setRole(currentAdmin.getRole());
		statusService.add(status);
		logger.info("{} has added status with name: {}", currentAdmin.getFullName(), statusName);
		return ResponseEntity.ok("Успешно добавлено");
	}

	@PostMapping(value = "/client/change")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity changeClientStatus(@RequestParam(name = "statusId") Long statusId,
											 @RequestParam(name = "clientId") Long clientId,
											 @AuthenticationPrincipal User userFromSession) {
		Client currentClient = clientService.get(clientId);
		if (currentClient.getStatus().getId().equals(statusId)) {
			return ResponseEntity.ok().build();
		}
		Status lastStatus = currentClient.getStatus();
		statusService.get(statusId).ifPresent(currentClient::setStatus);
		clientHistoryService.createHistoryOfChangingStatus(userFromSession, currentClient, lastStatus).ifPresent(currentClient::addHistory);
		if (!lastStatus.isCreateStudent() && currentClient.getStatus().isCreateStudent()) {
			Optional<Student> newStudent = studentService.addStudentForClient(currentClient);
			if (newStudent.isPresent()) {
                clientHistoryService.creteStudentHistory(userFromSession, ClientHistory.Type.ADD_STUDENT).ifPresent(currentClient::addHistory);
				clientService.updateClient(currentClient);
				notificationService.deleteNotificationsByClient(currentClient);
				logger.info("{} has changed status of client with id: {} to status id: {}", userFromSession.getFullName(), clientId, statusId);
				return ResponseEntity.ok().build();
			}
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		clientService.updateClient(currentClient);
		notificationService.deleteNotificationsByClient(currentClient);
		logger.info("{} has changed status of client with id: {} to status id: {}", userFromSession.getFullName(), clientId, statusId);
		return ResponseEntity.ok().build();
	}

	@PostMapping(value = "/client/delete")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity deleteClientStatus(@RequestParam("clientId") long clientId,
											 @AuthenticationPrincipal User userFromSession) {
		Client client = clientService.get(clientId);
		if (client == null) {
			logger.error("Can`t delete client status, client with id = {} not found", clientId);
			return ResponseEntity.notFound().build();
		}
		Status lastStatus = client.getStatus();
		statusService.get("deleted").ifPresent(client::setStatus);
		clientHistoryService.createHistoryOfChangingStatus(userFromSession, client, lastStatus).ifPresent(client::addHistory);
		clientService.updateClient(client);
		notificationService.deleteNotificationsByClient(client);
		logger.info("{} delete client with id = {} in status {}", userFromSession.getFullName(), client.getId(), lastStatus.getName());
		return ResponseEntity.ok().build();
	}

	@PostMapping(value = "/position/change")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity changePositionOfTwoStatuses(@RequestParam("sourceId") long sourceId,
													  @RequestParam("destinationId") long destinationId) {
		Optional<Status> sourceStatus = statusService.get(sourceId);
		Optional<Status> destinationStatus = statusService.get(destinationId);
		if (!sourceStatus.isPresent() || !destinationStatus.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Long tempPosition = sourceStatus.get().getPosition();
		sourceStatus.get().setPosition(destinationStatus.get().getPosition());
		destinationStatus.get().setPosition(tempPosition);
		statusService.update(sourceStatus.get());
		return ResponseEntity.ok().build();
	}

	@PostMapping (value = "/create-student")
	@PreAuthorize ("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public HttpStatus setCreateStudent(@RequestParam("id") Long id, @RequestParam("create") Boolean create) {
		Optional<Status> status = statusService.get(id);
		if (status.isPresent()) {
			status.get().setCreateStudent(create);
			statusService.update(status.get());
			return HttpStatus.OK;
		}
		return HttpStatus.NOT_FOUND;
	}

	@PostMapping ("/client/changeByName")
	@PreAuthorize ("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity changeStatusByName(@RequestParam("newStatus") String newStatus,
										 @RequestParam("clientId") Long clientId,
										 @AuthenticationPrincipal User currentUser){

		Optional<Status> st = statusService.getStatusByName(newStatus);
		if (st.isPresent()) {
			Long statusId = st.get().getId();
			return this.changeClientStatus(statusId, clientId, currentUser);
		}
		return new ResponseEntity(HttpStatus.NOT_FOUND);
	}
}
