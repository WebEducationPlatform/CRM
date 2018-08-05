package com.ewp.crm.controllers;

import com.ewp.crm.service.interfaces.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ReportController {

    @Autowired
    private StatusService statusService;

    @RequestMapping(value = "/report", method = RequestMethod.GET)
    public ModelAndView trackingGroupInfo() {
        ModelAndView modelAndView = new ModelAndView("report-clients");
        modelAndView.addObject("statuses", statusService.getAll());
        return modelAndView;
    }
}
