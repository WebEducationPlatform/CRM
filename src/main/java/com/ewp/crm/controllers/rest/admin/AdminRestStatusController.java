package com.ewp.crm.controllers.rest.admin;

import com.ewp.crm.models.*;
import com.ewp.crm.service.interfaces.NotificationService;
import com.ewp.crm.service.interfaces.StatusService;
import com.ewp.crm.service.interfaces.UserStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'HR')")
@RequestMapping("/admin/rest/status")
public class AdminRestStatusController {

	private static Logger logger = LoggerFactory.getLogger(AdminRestStatusController.class);

	private final StatusService statusService;
	private final NotificationService notificationService;
	private final UserStatusService userStatusService;

	@Autowired
	public AdminRestStatusController(StatusService statusService,
									 NotificationService notificationService,
									 UserStatusService userStatusService) {
		this.statusService = statusService;
		this.notificationService = notificationService;
		this.userStatusService = userStatusService;
	}

	@PostMapping(value = "/edit")
	public ResponseEntity editStatus(@Valid @RequestBody Status newStatus,
									 @AuthenticationPrincipal User currentAdmin) {

		Optional<Status> status = statusService.get(newStatus.getId());
		if (status.isPresent()) {
			status.get().setName(newStatus.getName());
			status.get().setTrialOffset(newStatus.getTrialOffset());
			status.get().setNextPaymentOffset(newStatus.getNextPaymentOffset());
			status.get().setRole(newStatus.getRole());
			statusService.update(status.get());
			logger.info("{} has updated status {}", currentAdmin.getFullName(), newStatus.getName());
			return ResponseEntity.ok().build();
		}
		return new ResponseEntity(HttpStatus.NOT_FOUND);
	}

	@PostMapping(value = "/delete")
	public ResponseEntity deleteStatus(@RequestParam(name = "deleteId") Long deleteId,
									   @AuthenticationPrincipal User currentAdmin) {
		Optional<Status> status = statusService.get(deleteId);
		if (status.isPresent()) {
			List<Client> clients = status.get().getClients();
			for (Client client : clients) {
				notificationService.deleteNotificationsByClient(client);
			}
			try {
				statusService.delete(deleteId);
				userStatusService.deleteStatus(deleteId);
			}catch (Exception e) {
				logger.error(e.getMessage());
			}
			logger.info("{} has  deleted status  with id {}", currentAdmin.getFullName(), deleteId);
			return ResponseEntity.ok().build();
		}
		return new ResponseEntity(HttpStatus.NOT_FOUND);
	}

	@PostMapping(value = "/visible/change")
	public ResponseEntity changeVisibleStatus(@RequestParam("statusId") long statusId,
											  @RequestParam("invisible") boolean bool,
											  @AuthenticationPrincipal User currentAdmin) {
		UserStatus userStatus = userStatusService.getStatus(statusId, currentAdmin.getId());
		Optional<Status> status = statusService.get(statusId);
		status.get().setInvisible(userStatus.getStatus_visible());
		status.get().setPosition(userStatus.getStatus_position());
		if (status.isPresent()) {
			if (status.get().getInvisible() == bool) {
				String reason = "Статус уже " + (bool ? "видимый" : "невидимый");
				logger.error(reason);
				return ResponseEntity.badRequest().body(reason);
			}
			List<Client> clients = status.get().getClients();
			for (Client client : clients) {
				notificationService.deleteNotificationsByClient(client);
			}
			status.get().setInvisible(bool);
			userStatusService.updateUserStatus(status.get(), currentAdmin);
			return ResponseEntity.ok().body(status);
		}
		return new ResponseEntity(HttpStatus.NOT_FOUND);
	}

}
