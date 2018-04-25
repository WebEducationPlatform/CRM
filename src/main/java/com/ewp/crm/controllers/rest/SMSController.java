package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Client;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.SMSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@RestController()
@RequestMapping("/user/sms")
public class SMSController {

	public static Logger logger = LoggerFactory.getLogger(SMSController.class);

	private final SMSService smsService;
	private final ClientService clientService;

	@Autowired
	public SMSController(SMSService smsService, ClientService clientService) {
		this.smsService = smsService;
		this.clientService = clientService;
	}

	@PostMapping("/now/client/{clientId}")
	public ResponseEntity<String> sendSMS(@PathVariable("clientId") long id, @RequestParam("message") String message) {
		Client client = clientService.getClientByID(id);
		String response = smsService.sendSMS(client, message);
		if (!response.equals("ok")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("/schedule/client/{clientId}")
	public ResponseEntity<String> sendScheduleSMS(@PathVariable("clientId") Long id,
	                                              @RequestParam("message") String message,
	                                              @RequestParam("date") String date) {
		Client client = clientService.getClientByID(id);
		String response = smsService.scheduledSMS(client, message, date);
		if (!response.equals("ok")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("/now/clients/{listClientsId}")
	public ResponseEntity<String> sendSMS(@PathVariable("listClientsId") List<Long> listClientsId, @RequestParam("message") String message) {
		List<Client> clients = new LinkedList<>();
		for (Long id : listClientsId) {
			clients.add(clientService.getClientByID(id));
		}
		String response = smsService.sendSMS(clients, message);
		if (!response.equals("ok")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("/schedule/clients/{listClientsId}")
	public ResponseEntity<String> sendScheduleSMS(@PathVariable("listClientsId") List<Long> listClientsId,
	                                              @RequestParam("message") String message,
	                                              @RequestParam("date") String date) {
		List<Client> clients = new LinkedList<>();
		for (Long id : listClientsId) {
			clients.add(clientService.getClientByID(id));
		}
		String response = smsService.scheduledSMS(clients,message,date);
		if (!response.equals("ok")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/balance")
	public ResponseEntity<String> getBalance(){
		String response = smsService.getBalance();
		if(response.contains("balance")){
			return ResponseEntity.ok(response);
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}

}
