package com.ewp.crm.controllers;

import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.MessageTemplateService;
import com.ewp.crm.service.interfaces.StatusService;
import com.ewp.crm.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/calls")
public class IPTelephonyController {

    private MessageTemplateService messageTemplateService;
    private StatusService statusService;
    private ClientService clientService;
    private UserService userService;

    @Autowired
    public IPTelephonyController(MessageTemplateService messageTemplateService, StatusService statusService, ClientService clientService, UserService userService) {
        this.messageTemplateService = messageTemplateService;
        this.statusService = statusService;
        this.clientService = clientService;
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'MENTOR')")
    public ModelAndView getPage() {
        ModelAndView modelAndView = new ModelAndView("calls");
        modelAndView.addObject("allClients", clientService.getAll());
        modelAndView.addObject("allUsers", userService.getAll());
        modelAndView.addObject("emailTmpl", messageTemplateService.getAll());
        modelAndView.addObject("statuses", statusService.getAll());
        return modelAndView;
    }
}