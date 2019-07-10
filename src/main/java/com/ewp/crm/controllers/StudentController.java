package com.ewp.crm.controllers;

import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.*;
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
@RequestMapping("/student")
@PreAuthorize("hasAnyAuthority('OWNER', 'HR')")
public class StudentController {

    private static Logger logger = LoggerFactory.getLogger(ClientController.class);

    private final StudentService studentService;
    private final StatusService statusService;
    private final MessageTemplateService messageTemplateService;
    private final ProjectPropertiesService projectPropertiesService;
    private final SlackService slackService;

    @Autowired
    public StudentController(StudentService studentService, StatusService statusService, SlackService slackService,
                             MessageTemplateService messageTemplateService, ProjectPropertiesService projectPropertiesService) {
        this.slackService = slackService;
        this.studentService = studentService;
        this.statusService = statusService;
        this.messageTemplateService = messageTemplateService;
        this.projectPropertiesService = projectPropertiesService;
    }

    @GetMapping(value = "/all")
    public ModelAndView showAllStudents(@AuthenticationPrincipal User userFromSession) {
        ModelAndView modelAndView = new ModelAndView("all-students-table");
        modelAndView.addObject("filters", userFromSession.getStudentPageFilters() != null ? userFromSession.getStudentPageFilters() : "");
        Long defaultStatusId = projectPropertiesService.getOrCreate().getClientRejectStudentStatus();
        if (defaultStatusId != null) {
            statusService.get(defaultStatusId).ifPresent(s -> modelAndView.addObject("defaultStatusForRejectedStudent", s));
        } else {
            modelAndView.addObject("defaultStatusForRejectedStudent", "");
        }
        modelAndView.addObject("students", studentService.getAll());
        modelAndView.addObject("statuses", statusService.getAll());
        modelAndView.addObject("emailTmpl", messageTemplateService.getAll());
        modelAndView.addObject("slackWorkspaceUrl", slackService.getSlackWorkspaceUrl());
        return modelAndView;
    }
}
