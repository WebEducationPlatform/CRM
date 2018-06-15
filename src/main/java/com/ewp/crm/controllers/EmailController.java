package com.ewp.crm.controllers;

import com.ewp.crm.configs.ImageConfig;
import com.ewp.crm.models.MessageTemplate;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.MessageTemplateService;
import com.ewp.crm.service.interfaces.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/admin")
public class EmailController {

	private final MessageTemplateService MessageTemplateService;
	private final ImageConfig imageConfig;
	private final NotificationService notificationService;

	@Autowired
	public EmailController(MessageTemplateService MessageTemplateService, ImageConfig imageConfig, NotificationService notificationService) {
		this.MessageTemplateService = MessageTemplateService;
		this.imageConfig = imageConfig;
		this.notificationService = notificationService;
	}


	@RequestMapping(value = {"/editMessageTemplate/{templateId}"}, method = RequestMethod.GET)
	public ModelAndView editTemplatePage(@PathVariable("templateId") Long templateId) {
		User userFromSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		MessageTemplate MessageTemplate = MessageTemplateService.get(templateId);
		ModelAndView modelAndView = new ModelAndView("edit-eTemplate");
		modelAndView.addObject("template", MessageTemplate);
		modelAndView.addObject("maxSize", imageConfig.getMaxImageSize());
		modelAndView.addObject("notifications", notificationService.getByUserToNotify(userFromSession));

		return modelAndView;
	}
}
