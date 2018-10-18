package com.ewp.crm.controllers;

import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.MessageTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/template")
@PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'OWNER')")
public class MessageTemplateController {

    private static Logger logger = LoggerFactory.getLogger(MessageTemplateController.class);

    private final MessageTemplateService messageTemplateService;

    @Autowired
    public MessageTemplateController(MessageTemplateService messageTemplateService) {
        this.messageTemplateService = messageTemplateService;
    }

    @GetMapping(value = "/all")
    public ModelAndView showMessageTemplatePage (@AuthenticationPrincipal User userFromSession) {
        ModelAndView modelAndView = new ModelAndView("all-templates");
        modelAndView.addObject("emailTmpl", messageTemplateService.getAll());
        modelAndView.addObject("user", userFromSession);
        return modelAndView;
    }
}
