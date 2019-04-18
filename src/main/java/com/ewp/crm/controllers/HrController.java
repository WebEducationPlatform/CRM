package com.ewp.crm.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/hr")
@PreAuthorize("hasAnyAuthority('OWNER','HR')")
public class HrController {
    private static Logger logger = LoggerFactory.getLogger(HrController.class);

    @GetMapping("/students")
    public ModelAndView showAllStudents() {
        return new ModelAndView("main-client-table-hr");
    }
}
