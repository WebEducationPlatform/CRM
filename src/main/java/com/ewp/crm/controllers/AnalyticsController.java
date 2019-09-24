package com.ewp.crm.controllers;

import com.ewp.crm.service.interfaces.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/analytics")
@PreAuthorize("hasAnyAuthority('OWNER','ADMIN')")
public class AnalyticsController {

    private final StatusService statusService;

    @Autowired
    public AnalyticsController(StatusService statusService) {
        this.statusService = statusService;
    }

    @GetMapping
    public ModelAndView showAnalytics() {
        final ModelAndView modelAndView = new ModelAndView("analytics-table");
        return modelAndView;
    }

}
