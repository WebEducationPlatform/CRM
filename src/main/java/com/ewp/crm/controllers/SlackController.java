package com.ewp.crm.controllers;

import com.ewp.crm.service.interfaces.SlackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SlackController {

    private final SlackService slackService;

    @Autowired
    public SlackController(SlackService slackService) {
        this.slackService = slackService;
    }

    @GetMapping("/slack/register")
    public ModelAndView registrationPage() {
        ModelAndView model = new ModelAndView("slack-registration");
        return model;
    }
}
