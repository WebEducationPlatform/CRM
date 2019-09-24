package com.ewp.crm.controllers;

import com.ewp.crm.configs.ImageConfig;
import com.ewp.crm.models.MessageTemplate;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.MessageTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/template")
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'HR', 'MENTOR')")
public class MessageTemplateController {

    private static Logger logger = LoggerFactory.getLogger(MessageTemplateController.class);

    private final MessageTemplateService messageTemplateService;
    private final ImageConfig imageConfig;

    @Autowired
    public MessageTemplateController(MessageTemplateService messageTemplateService,
                                     ImageConfig imageConfig) {
        this.messageTemplateService = messageTemplateService;
        this.imageConfig = imageConfig;
    }

    @GetMapping(value = "/all")
    public ModelAndView showMessageTemplatePage() {
        ModelAndView modelAndView = new ModelAndView("all-templates");
        return modelAndView;
    }

    @GetMapping(value = {"/edit/{templateId}"})
    public ModelAndView editTemplatePage(@PathVariable("templateId") Long templateId) {
        MessageTemplate messageTemplate = messageTemplateService.get(templateId);
        return getModelAndView(messageTemplate);
    }
    @GetMapping(value = {"/create/{templateName}"})
    public ModelAndView editTemplatePage(@PathVariable String templateName) {
        MessageTemplate messageTemplate = new MessageTemplate(templateName);
        return getModelAndView(messageTemplate);
    }

    private ModelAndView getModelAndView(MessageTemplate messageTemplate) {
        ModelAndView modelAndView = new ModelAndView("edit-eTemplate");
        modelAndView.addObject("template", messageTemplate);
        modelAndView.addObject("maxSize", imageConfig.getMaxImageSize());
        return modelAndView;
    }
}
