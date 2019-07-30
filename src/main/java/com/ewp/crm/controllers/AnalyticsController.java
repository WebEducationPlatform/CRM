package com.ewp.crm.controllers;

import com.ewp.crm.service.interfaces.StudentStatusService;
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

    private final StudentStatusService studentStatus;

    @Autowired
    public AnalyticsController(StudentStatusService studentStatus) {
        this.studentStatus = studentStatus;
    }

    @GetMapping
    public ModelAndView showAllManagers() {
        final ModelAndView modelAndView = new ModelAndView("analytics-table");

        modelAndView.addObject("studentStatusList", studentStatus.getAll());

        return modelAndView;
    }

}
