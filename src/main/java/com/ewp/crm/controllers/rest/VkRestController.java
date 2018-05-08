package com.ewp.crm.controllers.rest;

import com.ewp.crm.component.util.VKUtil;
import com.ewp.crm.configs.ImageConfig;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.EmailTemplate;
import com.ewp.crm.service.impl.EmailTemplateServiceImpl;
import com.ewp.crm.service.interfaces.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
	private final EmailTemplateServiceImpl emailTemplateService;



	@Autowired
	public VkRestController(ClientService clientService, EmailTemplateServiceImpl emailTemplateService, VKUtil vkUtil1) {
		this.vkUtil = vkUtil1;
		this.clientService = clientService;
		this.emailTemplateService = emailTemplateService;
	}


	@RequestMapping(value = "/rest/sendVK", method = RequestMethod.POST)
	public ResponseEntity sendEmail(@RequestParam("clientId") Long clientId, @RequestParam("templateId") Long templateId) {
		Client client = clientService.getClientByID(clientId);
		String vkText = emailTemplateService.get(templateId).getOtherText();
		vkUtil.sendMessageToClient(client,vkText);
		return ResponseEntity.ok().build();
	}
}
