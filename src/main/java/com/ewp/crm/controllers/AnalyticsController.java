package com.ewp.crm.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/analytics")
@PreAuthorize("hasAnyAuthority('OWNER','ADMIN')")
public class AnalyticsController {

    @GetMapping
    public ModelAndView showAnalytics() {
        final ModelAndView modelAndView = new ModelAndView("analytics-table");

        return modelAndView;
    }

}
