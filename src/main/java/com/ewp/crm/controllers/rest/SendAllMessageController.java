package com.ewp.crm.controllers.rest;

import com.ewp.crm.component.util.VKUtil;
import com.ewp.crm.component.util.interfaces.SMSUtil;
import com.ewp.crm.configs.ImageConfig;
import com.ewp.crm.service.email.MailSendService;
import com.ewp.crm.service.impl.EmailTemplateServiceImpl;
import com.ewp.crm.service.interfaces.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

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


	@RequestMapping(value = "/rest/sendSeveralMessage", method = RequestMethod.POST)
	public ResponseEntity sendSeveralMessage(@RequestBody String[] boxList) {
		if (Arrays.asList(boxList).contains("vk")) {
			System.out.println("VKесть!");
		}
		if (Arrays.asList(boxList).contains("facebook")) {
			System.out.println("FBесть!");
		}
		if (Arrays.asList(boxList).contains("email")) {
			System.out.println("EMAILесть!");
		}
		if (Arrays.asList(boxList).contains("sms")) {
			System.out.println("SMSесть!");
		}
		return ResponseEntity.ok(HttpStatus.OK);
	}
}



