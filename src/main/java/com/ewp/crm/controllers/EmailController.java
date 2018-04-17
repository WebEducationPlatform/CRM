package com.ewp.crm.controllers;

import com.ewp.crm.configs.ImageConfig;
import com.ewp.crm.models.EmailTemplate;
import com.ewp.crm.service.interfaces.EmailTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin")
public class EmailController {

	private final EmailTemplateService emailTemplateService;
	private final ImageConfig imageConfig;

	@Autowired
	public EmailController(EmailTemplateService emailTemplateService, ImageConfig imageConfig) {
		this.emailTemplateService = emailTemplateService;
		this.imageConfig = imageConfig;
	}


	@RequestMapping(value = {"/editEmailTemplate/{templateId}"}, method = RequestMethod.GET)
	public ModelAndView editTemplatePage(@PathVariable("templateId") Long templateId) {
		EmailTemplate emailTemplate = emailTemplateService.get(templateId);
		ModelAndView modelAndView = new ModelAndView("edit-eTemplate");
		modelAndView.addObject("template", emailTemplate);
		modelAndView.addObject("maxSize", imageConfig.getMaxImageSize());
		return modelAndView;
	}
}
