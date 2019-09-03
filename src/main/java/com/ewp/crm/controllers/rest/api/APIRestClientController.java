package com.ewp.crm.controllers.rest.api;

import com.ewp.crm.models.Client;
import com.ewp.crm.service.interfaces.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rest/api/client")
public class APIRestClientController {

		@Autowired
	ClientService clientService;

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER','MENTOR','HR')")
	public ResponseEntity<List<Client>> getAll() {
		return ResponseEntity.ok(clientService.getAll());
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER','MENTOR','HR')")
	public ResponseEntity<Client> getClientByID(@PathVariable Long id) {
		return ResponseEntity.ok(clientService.get(id));
	}
}
