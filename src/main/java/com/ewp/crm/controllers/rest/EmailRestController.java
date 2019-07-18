package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.MessageTemplate;
import com.ewp.crm.models.User;
import com.ewp.crm.service.impl.MessageTemplateServiceImpl;
import com.ewp.crm.service.interfaces.MailSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class EmailRestController {

	private final MailSendService mailSendService;
	private final MessageTemplateServiceImpl messageTemplateService;

	@Autowired
	public EmailRestController(MailSendService mailSendService,
							   MessageTemplateServiceImpl MessageTemplateService) {
		this.mailSendService = mailSendService;
		this.messageTemplateService = MessageTemplateService;
	}

	@PostMapping(value = "/rest/sendEmail")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'HR')")
	public ResponseEntity sendEmail(@RequestParam("clientId") Long clientId,
									@RequestParam("templateId") Long templateId,
									@RequestParam(value = "body", required = false) String body,
									@AuthenticationPrincipal User userFromSession) {
		MessageTemplate messageTemplate = messageTemplateService.get(templateId);
		String templateText = messageTemplate.getTemplateText();
		String theme = messageTemplate.getTheme();
		mailSendService.prepareAndSend(clientId, templateText, body, userFromSession, theme);
		return ResponseEntity.ok().build();
	}
}
