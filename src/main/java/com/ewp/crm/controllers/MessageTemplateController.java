package com.ewp.crm.controllers;

import com.ewp.crm.configs.ImageConfig;
import com.ewp.crm.models.MessageTemplate;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.MessageTemplateService;
import com.ewp.crm.service.interfaces.NotificationService;
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
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'HR','MENTOR')")
public class MessageTemplateController {

    private static Logger logger = LoggerFactory.getLogger(MessageTemplateController.class);

    private final MessageTemplateService messageTemplateService;
    private final ImageConfig imageConfig;
    private final NotificationService notificationService;

    @Autowired
    public MessageTemplateController(MessageTemplateService messageTemplateService,
                                     ImageConfig imageConfig,
                                     NotificationService notificationService) {
        this.messageTemplateService = messageTemplateService;
        this.imageConfig = imageConfig;
        this.notificationService = notificationService;
    }

    @GetMapping(value = "/all")
    public ModelAndView showMessageTemplatePage(@AuthenticationPrincipal User userFromSession) {
        ModelAndView modelAndView = new ModelAndView("all-templates");
        modelAndView.addObject("emailTmpl", messageTemplateService.getAll());
        modelAndView.addObject("user", userFromSession);
        return modelAndView;
    }

    @GetMapping(value = {"/edit/{templateId}"})
    public ModelAndView editTemplatePage(@PathVariable("templateId") Long templateId,
                                         @AuthenticationPrincipal User userFromSession) {
        MessageTemplate messageTemplate = messageTemplateService.get(templateId);
        return getModelAndView(userFromSession, messageTemplate);
    }
    @GetMapping(value = {"/create/{templateName}"})
    public ModelAndView editTemplatePage(@AuthenticationPrincipal User userFromSession, @PathVariable String templateName) {
        MessageTemplate messageTemplate = new MessageTemplate(templateName);
        return getModelAndView(userFromSession, messageTemplate);
    }

    private ModelAndView getModelAndView(@AuthenticationPrincipal User userFromSession, MessageTemplate messageTemplate) {
        ModelAndView modelAndView = new ModelAndView("edit-eTemplate");
        modelAndView.addObject("template", messageTemplate);
        modelAndView.addObject("maxSize", imageConfig.getMaxImageSize());
        modelAndView.addObject("notifications", notificationService.getByUserToNotify(userFromSession));
        return modelAndView;
    }
}
