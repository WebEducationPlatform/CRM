package com.ewp.crm.controllers;

import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.UserService;
import com.ewp.crm.service.interfaces.UserTaskService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserTaskController {

    private final UserService userService;
    private final ClientService clientService;
    private final UserTaskService userTaskService;

    public UserTaskController(UserService userService, ClientService clientService, UserTaskService userTaskService) {
        this.userService = userService;
        this.clientService = clientService;
        this.userTaskService = userTaskService;
    }


    @GetMapping(value = "/tasks")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'MENTOR', 'HR')")
    public ModelAndView allTasksPage(@AuthenticationPrincipal User currentUser) {
        ModelAndView modelAndView = new ModelAndView("user-tasks");
        modelAndView.addObject("tasklist", userTaskService.getAll());
        modelAndView.addObject("currentUserName", currentUser.getFullName());
        modelAndView.addObject("users", userService.getAll());
        return modelAndView;
    }
}
