package com.ewp.crm.controllers.rest.admin;

import com.ewp.crm.models.*;
import com.ewp.crm.service.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/admin/rest/client")
public class AdminRestClientController {

	private static Logger logger = LoggerFactory.getLogger(AdminRestClientController.class);

	private final ClientService clientService;
	private final SocialProfileTypeService socialProfileTypeService;
	private final ClientHistoryService clientHistoryService;
	private final StatusService statusService;

	@Autowired
	public AdminRestClientController(ClientService clientService,
									 SocialProfileTypeService socialProfileTypeService,
									 ClientHistoryService clientHistoryService,
									 StatusService statusService) {
		this.clientService = clientService;
		this.socialProfileTypeService = socialProfileTypeService;
		this.clientHistoryService = clientHistoryService;
		this.statusService = statusService;
	}

	@PostMapping(value = "/add")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
	public ResponseEntity addClient(@RequestBody Client client,
									@AuthenticationPrincipal User userFromSession) {
		for (SocialProfile socialProfile : client.getSocialProfiles()) {
			socialProfile.getSocialProfileType().setId(socialProfileTypeService.getByTypeName(
					socialProfile.getSocialProfileType().getName()).getId());
		}
		Status status = statusService.get(client.getStatus().getName());
		client.setStatus(status);
		client.addHistory(clientHistoryService.createHistory(userFromSession, client, ClientHistory.Type.ADD));
		clientService.addClient(client);
		logger.info("{} has added client: id {}, email {}", userFromSession.getFullName(), client.getId(), client.getEmail());
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@PostMapping(value = "/update")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
	public ResponseEntity updateClient(@RequestBody Client currentClient,
									   @AuthenticationPrincipal User userFromSession) {
		for (SocialProfile socialProfile : currentClient.getSocialProfiles()) {
			socialProfile.getSocialProfileType().setId(socialProfileTypeService.getByTypeName(
					socialProfile.getSocialProfileType().getName()).getId());
		}
		Client clientFromDB = clientService.get(currentClient.getId());
		currentClient.setHistory(clientFromDB.getHistory());
		currentClient.setComments(clientFromDB.getComments());
		currentClient.setOwnerUser(clientFromDB.getOwnerUser());
		currentClient.setStatus(clientFromDB.getStatus());
		currentClient.setDateOfRegistration(ZonedDateTime.parse(clientFromDB.getDateOfRegistration().toString()));
		currentClient.setSmsInfo(clientFromDB.getSmsInfo());
		currentClient.setNotifications(clientFromDB.getNotifications());
		currentClient.setCanCall(clientFromDB.isCanCall());
		currentClient.setCallRecords(clientFromDB.getCallRecords());
		currentClient.setClientDescriptionComment(clientFromDB.getClientDescriptionComment());
		if (currentClient.equals(clientFromDB)) {
			return ResponseEntity.noContent().build();
		}
		currentClient.addHistory(clientHistoryService.createHistory(userFromSession, clientFromDB, currentClient, ClientHistory.Type.UPDATE));
		clientService.updateClient(currentClient);
		logger.info("{} has updated client: id {}, email {}", userFromSession.getFullName(), currentClient.getId(), currentClient.getEmail());
		return ResponseEntity.ok(HttpStatus.OK);
	}

}