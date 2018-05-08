package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.component.util.interfaces.SMSUtil;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/user/sms")
public class SMSController {

	private final SMSUtil smsUtil;
	private final ClientService clientService;

	@Autowired
	public SMSController(SMSUtil smsUtil, ClientService clientService) {
		this.smsUtil = smsUtil;
		this.clientService = clientService;
	}

	@PostMapping("/send/now/client/{clientId}")
	public ResponseEntity<String> sendSMS(@PathVariable("clientId") long id, @RequestParam("message") String message) {
		User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Client client = clientService.getClientByID(id);
		smsUtil.sendSMS(client, message, principal);
		clientService.updateClient(client);
		return ResponseEntity.status(HttpStatus.OK).body("Message send");
	}

	@PostMapping("/send/planned/client/{clientId}")
	public ResponseEntity<String> plannedSMS(@PathVariable("clientId") Long id,
	                                         @RequestParam("message") String message,
	                                         @RequestParam("date") String date) {
		User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Client client = clientService.getClientByID(id);
		DateTime utc = DateTime.parse(date);
		smsUtil.plannedSMS(client, message, utc.toString("yyyy-MM-dd'T'HH:mm:ss'Z'"), principal);
		clientService.updateClient(client);
		return ResponseEntity.status(HttpStatus.OK).body("Send Message");
	}

	@PostMapping("/send/now/clients/{listClientsId}")
	public ResponseEntity<String> sendSMS(@PathVariable("listClientsId") List<Long> listClientsId, @RequestParam("message") String message) {
		User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Client> clients = new LinkedList<>();
		for (Long id : listClientsId) {
			clients.add(clientService.getClientByID(id));
		}
		smsUtil.sendSMS(clients, message, principal);
		for (Client client : clients) {
			clientService.updateClient(client);
		}
		return ResponseEntity.status(HttpStatus.OK).body("Messages in queue");
	}

	@PostMapping("/send/planned/clients/{listClientsId}")
	public ResponseEntity<String> plannedSMS(@PathVariable("listClientsId") List<Long> listClientsId,
	                                         @RequestParam("message") String message,
	                                         @RequestParam("date") String date) {
		User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Client> clients = new LinkedList<>();
		DateTime utc = DateTime.parse(date);
		for (Long id : listClientsId) {
			clients.add(clientService.getClientByID(id));
		}
		smsUtil.plannedSMS(clients, message, utc.toString("yyyy-MM-dd'T'HH:mm:ss'Z'"), principal);
		for (Client client : clients) {
			clientService.updateClient(client);
		}
		return ResponseEntity.status(HttpStatus.OK).body("Message send");
	}

	@GetMapping("/balance")
	public ResponseEntity<String> getBalance() {
		String response = smsUtil.getBalance();
		if (response.contains("balance")) {
			return ResponseEntity.ok(response);
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}

}
