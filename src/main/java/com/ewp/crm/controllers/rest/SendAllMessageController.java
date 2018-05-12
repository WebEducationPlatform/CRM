package com.ewp.crm.controllers.rest;

import com.ewp.crm.component.util.VKUtil;
import com.ewp.crm.component.util.interfaces.SMSUtil;
import com.ewp.crm.configs.ImageConfig;
import com.ewp.crm.models.Client;
import com.ewp.crm.service.email.MailSendService;
import com.ewp.crm.service.impl.EmailTemplateServiceImpl;
import com.ewp.crm.service.interfaces.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
public class SendAllMessageController {

	private final MailSendService mailSendService;
	private final EmailTemplateServiceImpl emailTemplateService;
	private final ClientService clientService;
	private final ImageConfig imageConfig;
	private final SMSUtil smsUtil;
	private final VKUtil vkUtil ;


	@Autowired
	public SendAllMessageController(MailSendService mailSendService, EmailTemplateServiceImpl emailTemplateService, ClientService clientService, ImageConfig imageConfig, SMSUtil smsUtil, VKUtil vkUtil) {
		this.mailSendService = mailSendService;
		this.emailTemplateService = emailTemplateService;
		this.clientService = clientService;
		this.imageConfig = imageConfig;
		this.smsUtil = smsUtil;
		this.vkUtil = vkUtil;
	}


	@RequestMapping(value = "/rest/messages", method = RequestMethod.POST)
	public ResponseEntity sendSeveralMessage(@RequestParam("boxList") String boxList,
	                                         @RequestParam("clientId") Long clientId, @RequestParam("templateId") Long templateId) {
		if (boxList.contains("vk")) {
			Client client = clientService.getClientByID(clientId);
			String vkText = emailTemplateService.get(templateId).getOtherText();
			String fullName = client.getName() + " " + client.getLastName();
			Map<String, String> params1 = new HashMap<>();
			params1.put("%fullName%", fullName);
			for (Map.Entry<String, String> entry : params1.entrySet()) {
				vkText = String.valueOf(new StringBuilder(vkText.replaceAll(entry.getKey(), entry.getValue())));
				vkUtil.sendMessageToClient(client, vkText);
			}
		}
		if (boxList.contains("facebook")) {
			System.out.println("FBесть!");
		}
		if (boxList.contains("email")) {
			Client client = clientService.getClientByID(clientId);
			String fullName = client.getName() + " " + client.getLastName();
			Map<String, String> params = new HashMap<>();
			params.put("%fullName%", fullName);
			mailSendService.prepareAndSend(client.getEmail(), params, emailTemplateService.get(templateId).getTemplateText(),
					"emailStringTemplate");
		}
		if (boxList.contains("sms")) {
			System.out.println("SMSесть!");
		}
		return ResponseEntity.ok(HttpStatus.OK);
	}


	@RequestMapping(value = "/rest/messages/custom", method = RequestMethod.POST)
	public ResponseEntity sendSeveralMessage(@RequestParam("boxList") String boxList,
	                                         @RequestParam("clientId") Long clientId, @RequestParam("body") String body) {
		if (boxList.contains("vk")) {
			Client client = clientService.getClientByID(clientId);
			String vkText = emailTemplateService.get(1L).getOtherText();
			Map<String, String> params1 = new HashMap<>();
			params1.put("%bodyText%", body);
			for (Map.Entry<String, String> entry : params1.entrySet()) {
				vkText = String.valueOf(new StringBuilder(vkText.replaceAll(entry.getKey(), entry.getValue())));
				vkUtil.sendMessageToClient(client, vkText);
			}
		}
		if (boxList.contains("facebook")) {
			System.out.println("FBесть!");
		}
		if (boxList.contains("email")) {
			Client client = clientService.getClientByID(clientId);
			Map<String, String> params = new HashMap<>();
			params.put("%bodyText%", body);
			mailSendService.prepareAndSend(client.getEmail(), params, emailTemplateService.get(1L).getTemplateText(),
					"emailStringTemplate");
		}
		if (boxList.contains("sms")) {
			System.out.println("SMSесть!");
		}
		return ResponseEntity.ok(HttpStatus.OK);
	}
}