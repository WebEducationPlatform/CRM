package com.ewp.crm.controllers.rest;

import com.ewp.crm.exceptions.client.ClientException;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/rest/client")
public class RestClientController {
	private static Logger logger = LoggerFactory.getLogger(RestClientController.class);
	@Autowired
	private ClientService clientService;

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<List<Client>> getAll() {
		return ResponseEntity.ok(clientService.getAllClients());
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Client> getClientByID(@PathVariable Long id) {
		return ResponseEntity.ok(clientService.getClientByID(id));
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public ResponseEntity updateClient(@RequestBody Client client) {
		clientService.updateClient(client);
		User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		logger.info("Admin {} has updated client: id {}, email {}", currentAdmin.getEmail(), client.getId(), client.getEmail());
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
	public ResponseEntity deleteClient(@PathVariable Long id) {
		clientService.deleteClient(id);
		User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		logger.info("Admin {} has deleted client with id {}", currentAdmin.getEmail(), id);
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@RequestMapping(value = "/addClient", method = RequestMethod.POST)
	public ResponseEntity addClient(@RequestBody Client client) {
		clientService.addClient(client);
		User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		logger.info("Admin {} has added client: id {}, email {}", currentAdmin.getEmail(), client.getId(), client.getEmail());
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@ExceptionHandler(ClientException.class)
	public ResponseEntity error(ClientException e) {
		return ResponseEntity.badRequest().body(e.getMessage());
	}
}
