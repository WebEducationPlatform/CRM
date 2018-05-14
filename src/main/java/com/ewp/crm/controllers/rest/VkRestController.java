package com.ewp.crm.controllers.rest;

import com.ewp.crm.component.util.VKUtil;
import com.ewp.crm.configs.ImageConfig;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.MessageTemplate;
import com.ewp.crm.models.User;
import com.ewp.crm.service.impl.MessageTemplateServiceImpl;
import com.ewp.crm.service.interfaces.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
public class VkRestController {

	private final VKUtil vkUtil ;
	private final ClientService clientService;
	private final MessageTemplateServiceImpl MessageTemplateService;

	@Autowired
	public VkRestController(ClientService clientService, MessageTemplateServiceImpl MessageTemplateService, VKUtil vkUtil1) {
		this.vkUtil = vkUtil1;
		this.clientService = clientService;
		this.MessageTemplateService = MessageTemplateService;
	}

	@RequestMapping(value = "/rest/vkontakte", method = RequestMethod.POST)
	public ResponseEntity<String> sendToVkontakte(@RequestParam("clientId") Long clientId, @RequestParam("templateId") Long templateId) {
		User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Client client = clientService.getClientByID(clientId);
		String vkText = MessageTemplateService.get(templateId).getOtherText();
		String fullName = client.getName() + " " + client.getLastName();
		Map<String, String> params = new HashMap<>();
		params.put("%fullName%", fullName);

		vkUtil.sendMessageToClient(client, vkText, params, principal);
		return ResponseEntity.status(HttpStatus.OK).body("Message send successfully");
	}
}
