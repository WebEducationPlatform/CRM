package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.ProjectProperties;
import com.ewp.crm.models.Status;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.ClientHistoryService;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.ProjectPropertiesService;
import com.ewp.crm.service.interfaces.RoleService;
import com.ewp.crm.service.interfaces.StatusService;
import com.ewp.crm.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
public class SkypeCallRestController {
	private static Logger logger = LoggerFactory.getLogger(SkypeCallRestController.class);

	private final ClientHistoryService clientHistoryService;
	private final ClientService clientService;
	private final UserService userService;
	private final RoleService roleService;
	private final StatusService statusService;
	private final ProjectProperties projectProperties;
	private Environment env;

	@Autowired
	public SkypeCallRestController(ClientHistoryService clientHistoryService,
								   ClientService clientService,
								   UserService userService,
								   RoleService roleService, Environment env,
								   StatusService statusService,
								   ProjectPropertiesService projectPropertiesService) {
		this.clientHistoryService = clientHistoryService;
		this.clientService = clientService;
		this.userService = userService;
		this.roleService = roleService;
		this.env = env;
		this.statusService = statusService;
		this.projectProperties = projectPropertiesService.getOrCreate();
	}

	@GetMapping(value = "rest/skype/allMentors", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'HR')")
	public ResponseEntity<List<User>> getAllMentors() {
		List<User> users = userService.getByRole(roleService.getRoleByName("MENTOR"));
		return ResponseEntity.ok(users);
	}

	@PostMapping(value = "rest/skype/addSkypeCallAndNotification")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'HR')")
	public ResponseEntity addSkypeCallAndNotification(@AuthenticationPrincipal User userFromSession,
													  @RequestParam Long startDate,
													  @RequestParam Long clientId) {
		Optional<Client> optionalClient = clientService.getClientByID(clientId);
		if (optionalClient.isPresent()) {
			ZonedDateTime dateSkypeCall = Instant.ofEpochMilli(startDate).atZone(ZoneId.of("+00:00")).withZoneSameLocal(ZoneId.of("Europe/Moscow"));
			ZonedDateTime notificationBeforeOfSkypeCall = Instant.ofEpochMilli(startDate).atZone(ZoneId.of("+00:00")).withZoneSameLocal(ZoneId.of("Europe/Moscow")).minusHours(1);
			try {
				Client client = optionalClient.get();
				client.setLiveSkypeCall(true);
				clientHistoryService.createHistory(userFromSession, client, ClientHistory.Type.SKYPE).ifPresent(client::addHistory);
				clientService.update(client);
				logger.info("{} добавил клиенту id:{} звонок по скайпу на {}", userFromSession.getFullName(), client.getId(), dateSkypeCall);
				Long firstSkypeCallAfterStatus = projectProperties.getFirstSkypeCallAfterStatus();
				if (statusService.get(firstSkypeCallAfterStatus).isPresent()) {
					Status status = statusService.get(firstSkypeCallAfterStatus).get();
					client.setStatus(status);
					clientService.update(client);
					logger.info("{} статус клиента id:{} изменен на {} при назначении звонка по скайпу", userFromSession.getFullName(), client.getId(), status.getName());
				}
				return ResponseEntity.ok(HttpStatus.OK);
			} catch (Exception e) {
				logger.error("{} не смог добавить клиенту id:{} звокон по скайпу на {}", userFromSession.getFullName(), optionalClient.get().getId(), startDate, e);
				return ResponseEntity.badRequest().body(env.getProperty("messaging.skype.call.event-error"));
			}
		}
		logger.info("{} не смог добавить клиенту id:{} звокон по скайпу на {} (Клиент не найден)", userFromSession.getFullName(), clientId, startDate);
		return ResponseEntity.badRequest().body(env.getProperty("messaging.skype.call.event-error"));
	}

	@PostMapping(value = "rest/mentor/updateEvent")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'HR')")
	public ResponseEntity updateEvent(@AuthenticationPrincipal User userFromSession,
									  @RequestParam(name = "clientId") Long clientId,
									  @RequestParam Long skypeCallDateNew) {
		Client client = clientService.get(clientId);

		logger.info("{} не смог изменить клиенту id:{} звокон по скайпу на {}", userFromSession.getFullName(), client.getId(), skypeCallDateNew);
		return ResponseEntity.badRequest().body(env.getProperty("messaging.skype.call.event-error"));

	}

	@PostMapping(value = "rest/mentor/deleteEvent")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'HR')")
	public ResponseEntity deleteEvent(@AuthenticationPrincipal User principal,
									  @RequestParam(name = "clientId") Long clientId) {
	    Client client = clientService.get(clientId);
	    client.setLiveSkypeCall(false);
	    clientHistoryService.createHistory(principal, client, ClientHistory.Type.SKYPE_DELETE).ifPresent(client::addHistory);
	    clientService.updateClient(client);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}
}