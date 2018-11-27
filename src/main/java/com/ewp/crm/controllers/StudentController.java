package com.ewp.crm.controllers;

import com.ewp.crm.service.interfaces.StatusService;
import com.ewp.crm.service.interfaces.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/student")
@PreAuthorize("hasAnyAuthority('OWNER')")
public class StudentController {

    private static Logger logger = LoggerFactory.getLogger(ClientController.class);

    private final StudentService studentService;
    private final StatusService statusService;

    @Autowired
    public StudentController(StudentService studentService, StatusService statusService) {
        this.studentService = studentService;
        this.statusService = statusService;
    }

    @GetMapping(value = "/all")
    public ModelAndView showAllStudents() {
        ModelAndView modelAndView = new ModelAndView("all-students-table");
        modelAndView.addObject("students", studentService.getAll());
        modelAndView.addObject("statuses", statusService.getAll());
        return modelAndView;
    }
}
