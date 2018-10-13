package com.ewp.crm.controllers.rest;

import com.ewp.crm.service.interfaces.SMSService;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.User;
import com.ewp.crm.service.impl.MessageTemplateServiceImpl;
import com.ewp.crm.service.interfaces.ClientService;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
@RequestMapping("/user/sms")
public class SMSRestController {

	private final SMSService smsService;
	private final ClientService clientService;
	private final MessageTemplateServiceImpl messageTemplateService;

	private static Logger logger = LoggerFactory.getLogger(SMSRestController.class);


	@Autowired
	public SMSRestController(SMSService smsService,
							 ClientService clientService,
							 MessageTemplateServiceImpl messageTemplateService) {
		this.smsService = smsService;
		this.clientService = clientService;
		this.messageTemplateService = messageTemplateService;
	}

	@PostMapping("/send/now/client")
	public ResponseEntity<String> sendSMS(@RequestParam("clientId") Long clientId,
										  @RequestParam("templateId") Long templateId,
	                                      @RequestParam(value = "body",required = false) String body,
										  @AuthenticationPrincipal User userFromSession) {
		String smsTemplateText = messageTemplateService.get(templateId).getOtherText();
		try {
			smsService.sendSMS(clientId, smsTemplateText, body, userFromSession);
			return ResponseEntity.status(HttpStatus.OK).body("Message sent");
		} catch (JSONException e) {
			logger.error("Error to send message ", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Message not sent");
		}
	}

	@PostMapping("/send/planned/client/{clientId}")
	public ResponseEntity<String> plannedSMS(@PathVariable("clientId") Long id,
	                                         @RequestParam("message") String message,
	                                         @RequestParam("date") String date,
											 @AuthenticationPrincipal User userFromSession) {
		Client client = clientService.get(id);
		ZonedDateTime utc = ZonedDateTime.parse(date);
		smsService.plannedSMS(client, message, utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")), userFromSession);
		clientService.updateClient(client);
		return ResponseEntity.status(HttpStatus.OK).body("Send Message");
	}

	@PostMapping("/send/now/clients/{listClientsId}")
	public ResponseEntity<String> sendSMS(@PathVariable("listClientsId") List<Long> listClientsId,
										  @RequestParam("message") String message,
										  @AuthenticationPrincipal User userFromSession) {
		List<Client> clients = clientService.getClientsByManyIds(listClientsId);
		smsService.sendSMS(clients, message, userFromSession);
		clientService.updateBatchClients(clients);
		return ResponseEntity.status(HttpStatus.OK).body("Messages in queue");
	}

	@PostMapping("/send/planned/clients/{listClientsId}")
	public ResponseEntity<String> plannedSMS(@PathVariable("listClientsId") List<Long> listClientsId,
	                                         @RequestParam("message") String message,
	                                         @RequestParam("date") String date,
											 @AuthenticationPrincipal User userFromSession) {
		List<Client> clients = clientService.getClientsByManyIds(listClientsId);
		ZonedDateTime utc = ZonedDateTime.parse(date);
		smsService.plannedSMS(clients, message, utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")), userFromSession);
		clientService.updateBatchClients(clients);
		return ResponseEntity.status(HttpStatus.OK).body("Message send");
	}

	@GetMapping("/balance")
	public ResponseEntity<String> getBalance() {
		String response = smsService.getBalance();
		if (response.contains("balance")) {
			return ResponseEntity.ok(response);
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}

}
