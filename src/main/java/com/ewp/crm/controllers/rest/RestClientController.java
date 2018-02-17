package com.ewp.crm.controllers.rest;

import com.ewp.crm.exceptions.client.ClientException;
import com.ewp.crm.models.Client;
import com.ewp.crm.service.interfaces.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/rest/client")
public class RestClientController {
	@Autowired
	private ClientService clientService;

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<List<Client>> getAll() {
		return ResponseEntity.ok(clientService.getAllClients());
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Client> getClientByID(@PathVariable Long id) throws ClientException {
		return ResponseEntity.ok(clientService.getClientByID(id));
	}

	@RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
	public ResponseEntity updateUser(@PathVariable Long id, @RequestBody Client client) throws ClientException {
		clientService.updateClient(id, client);
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
	public ResponseEntity deleteUser(@PathVariable Long id) throws ClientException {
		clientService.deleteClient(id);
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@RequestMapping(value = "/addClient", method = RequestMethod.POST)
	public ResponseEntity addUser(@RequestBody Client client) throws ClientException {
		clientService.addClient(client);
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@ExceptionHandler(ClientException.class)
	public ResponseEntity error(ClientException e) {
		return ResponseEntity.badRequest().body(e.getMessage());
	}
}
