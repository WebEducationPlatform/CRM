package com.ewp.crm.controllers;

import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
public class ReportController {

    @Autowired
    private StatusService statusService;

    @RequestMapping(value = "/report", method = RequestMethod.GET)
    public ModelAndView trackingGroupInfo() {
        ModelAndView modelAndView = new ModelAndView("report-clients");
        modelAndView.addObject("statuses", statusService.getAll());
        User currentUser = ((User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal());
        modelAndView.addObject("currentMail", currentUser.getEmail());
        return modelAndView;
    }
}
