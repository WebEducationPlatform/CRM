package com.ewp.crm.controllers;

import com.ewp.crm.models.Client;
import com.ewp.crm.service.interfaces.ClientFeedbackService;
import com.ewp.crm.service.interfaces.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/feedback")
@PreAuthorize("hasAnyAuthority('OWNER','ADMIN', 'HR')")
public class FeedbackController {

    private static Logger logger = LoggerFactory.getLogger(ClientController.class);

    private final ClientService clientService;
    private final ClientFeedbackService clientFeedbackService;

    @Autowired
    public FeedbackController(ClientService clientService, ClientFeedbackService clientFeedbackService) {
        this.clientService = clientService;
        this.clientFeedbackService = clientFeedbackService;
    }

    @GetMapping(value = "/all")
    public ModelAndView showAllFeedback() {
        ModelAndView modelAndView = new ModelAndView("all-client-feedback-table");
        List<Client> noFeedback = new ArrayList<>();
        clientService.getAllClients().forEach(client -> {
            if(client.getFeedback().isEmpty()) {
                noFeedback.add(client);
            }
        });
        modelAndView.addObject("clients",noFeedback);
        modelAndView.addObject("feedbacks", clientFeedbackService.getAllFeedback());
        return modelAndView;
    }
}