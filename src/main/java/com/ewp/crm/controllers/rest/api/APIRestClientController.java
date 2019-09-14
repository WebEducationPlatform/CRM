package com.ewp.crm.controllers.rest.api;

import com.ewp.crm.controllers.rest.admin.AdminRestClientController;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.Status;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.StatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/rest/api/client")
public class APIRestClientController {
	private static Logger logger = LoggerFactory.getLogger(AdminRestClientController.class);
	private final ClientService clientService;
	private final StatusService statusService;

	@Autowired
	public APIRestClientController(StatusService statusService,
								   ClientService clientService) {
		this.statusService = statusService;
		this.clientService = clientService;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Client>> getAll() {
		return ResponseEntity.ok(clientService.getAll());
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Client> getClientByID(@PathVariable Long id) {
		return ResponseEntity.ok(clientService.get(id));
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity addClient(@RequestBody Client client) {
		clientService.add(client);
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@PutMapping(value = "/update")
	public ResponseEntity updateClient(@RequestBody Client currentClient) {
		Client clientFromDB = clientService.get(currentClient.getId());
		if (currentClient.equals(clientFromDB)) {
			logger.info("{} has no need to update client: id {}, email {}", currentClient.getId(), currentClient.getEmail().orElse("not found"));
			return ResponseEntity.noContent().build();
		} else {
			clientService.updateClient(currentClient);
			logger.info("{} has updated client: id {}, email {}", currentClient.getId(), currentClient.getEmail().orElse("not found"));
			return ResponseEntity.ok(HttpStatus.OK);
		}
	}

	@PostMapping(value = "/updatestatus/{statusId}")
	public ResponseEntity updateClientStatus(@RequestBody Client currentClient,
											 @PathVariable Long statusId) {
		Client clientFromDB = clientService.get(currentClient.getId());
		Status newStatus = statusService.get(statusId).orElse(null);
		clientFromDB.setStatus(newStatus);
		clientService.updateClient(clientFromDB);

		return ResponseEntity.ok(HttpStatus.OK);

	}

	@PostMapping("/{clientId}")
	public ResponseEntity removeClient(@PathVariable(name = "clientId") Long clientId) {
		Client clientFromDB = clientService.get(clientId);
		if (Objects.isNull(clientFromDB)) {
			return ResponseEntity.notFound().build();
		}
		clientService.delete(clientId);

		logger.info("{} has delete client: id {}, email {}", clientFromDB.getId(), clientFromDB.getEmail().orElse("not found"));
		return ResponseEntity.ok(HttpStatus.OK);
	}

}
