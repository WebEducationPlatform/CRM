package com.ewp.crm.controllers;

import com.ewp.crm.configs.ImageConfig;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.AutoAnswersService;
import com.ewp.crm.service.interfaces.MessageTemplateService;
import com.ewp.crm.service.interfaces.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/autoanswers")
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
public class AutoAnswersController {
    private static Logger logger = LoggerFactory.getLogger(MessageTemplateController.class);

    private final AutoAnswersService autoAnswersService;
    private final MessageTemplateService messageTemplateService;
    private final ImageConfig imageConfig;
    private final NotificationService notificationService;

    public AutoAnswersController(AutoAnswersService autoAnswersService, MessageTemplateService messageTemplateService, ImageConfig imageConfig, NotificationService notificationService) {
        this.autoAnswersService = autoAnswersService;
        this.messageTemplateService = messageTemplateService;
        this.imageConfig = imageConfig;
        this.notificationService = notificationService;
    }

    @GetMapping(value = "/all")
    public ModelAndView showAutoAnswersAll(@AuthenticationPrincipal User userFromSession) {
        ModelAndView modelAndView = new ModelAndView("autoAnswers");
        modelAndView.addObject("autoAnswers", autoAnswersService.getAll());
        modelAndView.addObject("user", userFromSession);
        return modelAndView;
    }
}
