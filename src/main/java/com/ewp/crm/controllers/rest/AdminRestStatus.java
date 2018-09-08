package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Status;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.ClientHistoryService;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.NotificationService;
import com.ewp.crm.service.interfaces.StatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
@RequestMapping("/admin/rest")
public class AdminRestStatus {

	private static Logger logger = LoggerFactory.getLogger(AdminRestStatus.class);

	private final StatusService statusService;
	private final NotificationService notificationService;

	@Autowired
	public AdminRestStatus(StatusService statusService,
						   NotificationService notificationService) {
		this.statusService = statusService;
		this.notificationService = notificationService;
	}

	@PostMapping(value = "/status/edit")
	public ResponseEntity editStatus(@RequestParam(name = "statusName") String statusName,
									 @RequestParam(name = "oldStatusId") Long oldStatusId,
									 @AuthenticationPrincipal User currentAdmin) {
		Status status = statusService.get(oldStatusId);
		status.setName(statusName);
		statusService.update(status);
		logger.info("{} has updated status {}", currentAdmin.getFullName(), statusName);
		return ResponseEntity.ok().build();
	}

	@PostMapping(value = "/status/delete")
	public ResponseEntity deleteStatus(@RequestParam(name = "deleteId") Long deleteId,
									   @AuthenticationPrincipal User currentAdmin) {
		Status status = statusService.get(deleteId);
		List<Client> clients = status.getClients();
		for (Client client : clients) {
			notificationService.deleteNotificationsByClient(client);
		}
		statusService.delete(deleteId);

		logger.info("{} has  deleted status  with id {}", currentAdmin.getFullName(), deleteId);
		return ResponseEntity.ok().build();
	}

	@PostMapping(value = "/status/visible/change")
	public ResponseEntity changeVisibleStatus(@RequestParam("statusId") long statusId,
											  @RequestParam("invisible") boolean bool) {
		Status status = statusService.get(statusId);
		if (status.getInvisible() == bool) {
			String reason = "Статус уже " + (bool ? "невидимый" : "видимый");
			logger.error(reason);
			return ResponseEntity.badRequest().body(reason);
		}
		List<Client> clients = status.getClients();
		for (Client client : clients) {
			notificationService.deleteNotificationsByClient(client);
		}
		status.setInvisible(bool);
		statusService.update(status);
		return ResponseEntity.ok().body(status);
	}

}
