package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.*;
import com.ewp.crm.service.email.MailSendService;
import com.ewp.crm.service.impl.EmailTemplateServiceImpl;
import com.ewp.crm.service.interfaces.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ClientRestController {

	private static Logger logger = LoggerFactory.getLogger(ClientRestController.class);

	private final ClientService clientService;
	private final MailSendService mailSendService;
	private final EmailTemplateServiceImpl emailTemplateService;

	@Autowired
	public ClientRestController(ClientService clientService, MailSendService mailSendService, EmailTemplateServiceImpl emailTemplateService) {
		this.clientService = clientService;
		this.mailSendService = mailSendService;
		this.emailTemplateService = emailTemplateService;
	}

	@RequestMapping(value = "/rest/client", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Client>> getAll() {
		return ResponseEntity.ok(clientService.getAllClients());
	}

	@RequestMapping(value = "/rest/client/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Client> getClientByID(@PathVariable Long id) {
		return ResponseEntity.ok(clientService.getClientByID(id));
	}

	@RequestMapping(value = "/rest/client/assign", method = RequestMethod.POST)
	public ResponseEntity<User> assign(@RequestParam(name = "clientId") Long clientId) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Client client = clientService.getClientByID(clientId);
		if (client.getOwnerUser() != null) {
			logger.info("User {} tried to assign a client with id {}, but client have owner", user.getEmail(), clientId);
			return ResponseEntity.badRequest().body(null);
		}
		client.setOwnerUser(user);
		clientService.updateClient(client);
		logger.info("User {} has assigned client with id {}", user.getEmail(), clientId);
		return ResponseEntity.ok(client.getOwnerUser());
	}

	@RequestMapping(value = "/rest/client/unassign", method = RequestMethod.POST)
	public ResponseEntity unassign(@RequestParam(name = "clientId") Long clientId) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Client client = clientService.getClientByID(clientId);
		if (client.getOwnerUser() == null) {
			logger.info("User {} tried to unassign a client with id {}, but client already doesn't have owner", user.getEmail(), clientId);
			return ResponseEntity.badRequest().build();
		}
		client.setOwnerUser(null);
		clientService.updateClient(client);
		logger.info("User {} has unassigned client with id {}", user.getEmail(), clientId);
		return ResponseEntity.ok(client.getOwnerUser());
	}

	@RequestMapping(value = "/admin/rest/client/update", method = RequestMethod.POST)
	public ResponseEntity updateClient(@RequestBody Client client) {
		User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Client currentClient = clientService.getClientByID(client.getId());
		client.setHistory(currentClient.getHistory());
		client.setComment(currentClient.getComment());
		client.setOwnerUser(currentClient.getOwnerUser());
		client.setStatus(currentClient.getStatus());
		client.addHistory(new ClientHistory(currentAdmin.getFullName() + " изменил профиль клиента"));
		clientService.updateClient(client);
		logger.info("{} has updated client: id {}, email {}", currentAdmin.getFullName(), client.getId(), client.getEmail());
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@RequestMapping(value = "/admin/rest/client/delete/{id}", method = RequestMethod.POST)
	public ResponseEntity deleteClient(@PathVariable Long id) {
		clientService.deleteClient(id);
		User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		logger.info("{} has deleted client with id {}", currentAdmin.getFullName(), id);
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@RequestMapping(value = "/rest/client/addClient", method = RequestMethod.POST)
	public ResponseEntity addClient(@RequestBody Client client) {
		User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		client.addHistory(new ClientHistory(currentAdmin.getFullName() + " добавил клиента"));
		clientService.addClient(client);
		logger.info("{} has added client: id {}, email {}", currentAdmin.getFullName(), client.getId(), client.getEmail());
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@RequestMapping(value = "/rest/client/filtration", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Client>> getAllWithConditions(@RequestBody FilteringCondition filteringCondition) {
		return ResponseEntity.ok(clientService.filteringClient(filteringCondition));
	}


	@RequestMapping(value = "/rest/client/sendEmail", method = RequestMethod.POST)
	public ResponseEntity sendEmail(@RequestParam("clientId") Long clientId, @RequestParam("templateName") String templateName) {
		Client client = clientService.getClientByID(clientId);
		String fullName = client.getName() + " " + client.getLastName();
		Map<String, String> params = new HashMap<>();
		params.put("fullName",fullName);
		mailSendService.prepareAndSend(client.getEmail(),params,emailTemplateService.getByName(templateName).getTemplateText(),
				"emailStringTemplate");
		return ResponseEntity.ok().build();
	}

	@RequestMapping(value = "/admin/client/customEmailTemplate", method = RequestMethod.POST)
	public ResponseEntity addSocialNetworkType(@RequestParam("clientId") Long clientId, @RequestParam("body") String body) {
		Client client = clientService.getClientByID(clientId);
		Map<String, String> params = new HashMap<>();
		params.put("bodyText",body);
		mailSendService.prepareAndSend(client.getEmail(),params,emailTemplateService.get(1L).getTemplateText(),
				"emailStringTemplate");
		return ResponseEntity.ok().build();
	}

	@RequestMapping(value = {"/admin/editEmailTemplate"}, method = RequestMethod.POST)
	public ResponseEntity editETemplate(@RequestParam("templateId") Long templateId, @RequestParam("templateText") String templateText) {
		EmailTemplate emailTemplate = emailTemplateService.get(templateId);
		emailTemplate.setTemplateText(templateText);
		emailTemplateService.update(emailTemplate);
		return ResponseEntity.ok().build();
	}
}
