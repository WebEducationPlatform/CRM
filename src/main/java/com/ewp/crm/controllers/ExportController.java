package com.ewp.crm.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER')")
public class ExportController {

    @GetMapping(value = "/export")
    public ModelAndView export() {
        ModelAndView modelAndView = new ModelAndView("export");
        return modelAndView;
    }

}
