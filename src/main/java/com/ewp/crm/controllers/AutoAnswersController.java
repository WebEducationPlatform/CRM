package com.ewp.crm.controllers;

import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.AutoAnswersService;
import com.ewp.crm.service.interfaces.MessageTemplateService;
import com.ewp.crm.service.interfaces.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final AutoAnswersService autoAnswersService;
    private final MessageTemplateService messageTemplateService;
    private final StatusService statusService;

    @Autowired
    public AutoAnswersController(AutoAnswersService autoAnswersService, MessageTemplateService messageTemplateService,
                                 StatusService statusService) {
        this.autoAnswersService = autoAnswersService;
        this.messageTemplateService = messageTemplateService;
        this.statusService = statusService;
    }

    @GetMapping(value = "/all")
    public ModelAndView showAutoAnswersAll(@AuthenticationPrincipal User userFromSession) {
        ModelAndView modelAndView = new ModelAndView("autoAnswers");
        modelAndView.addObject("autoAnswers", autoAnswersService.getAll());
        modelAndView.addObject("statuses", statusService.getAll());
        modelAndView.addObject("templates", messageTemplateService.getAll());
        modelAndView.addObject("user", userFromSession);
        return modelAndView;
    }
}
