package com.ewp.crm.controllers;

import com.ewp.crm.configs.ImageConfig;
import com.ewp.crm.models.MessageTemplate;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.MessageTemplateService;
import com.ewp.crm.service.interfaces.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
@RequestMapping("/admin")
public class EmailController {

	private final MessageTemplateService messageTemplateService;
	private final ImageConfig imageConfig;
	private final NotificationService notificationService;

	@Autowired
	public EmailController(MessageTemplateService messageTemplateService,
						   ImageConfig imageConfig,
						   NotificationService notificationService) {
		this.messageTemplateService = messageTemplateService;
		this.imageConfig = imageConfig;
		this.notificationService = notificationService;
	}


	@GetMapping(value = {"/editMessageTemplate/{templateId}"})
	public ModelAndView editTemplatePage(@PathVariable("templateId") Long templateId,
										 @AuthenticationPrincipal User userFromSession) {
		MessageTemplate messageTemplate = messageTemplateService.get(templateId);
		ModelAndView modelAndView = new ModelAndView("edit-eTemplate");
		modelAndView.addObject("template", messageTemplate);
		modelAndView.addObject("maxSize", imageConfig.getMaxImageSize());
		modelAndView.addObject("notifications", notificationService.getByUserToNotify(userFromSession));
		return modelAndView;
	}
}
