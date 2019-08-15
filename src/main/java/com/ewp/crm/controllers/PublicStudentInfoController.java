package com.ewp.crm.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/public")
public class PublicStudentInfoController {

    @GetMapping
    public ModelAndView getPublicStudentInfoPage() {
        return new ModelAndView("public-student-info");
    }
}