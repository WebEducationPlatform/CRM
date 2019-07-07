package com.ewp.crm.controllers;

import com.ewp.crm.models.Role;
import com.ewp.crm.models.User;
import com.ewp.crm.models.dto.StatusDtoForBoard;
import com.ewp.crm.service.interfaces.MessageTemplateService;
import com.ewp.crm.service.interfaces.RoleService;
import com.ewp.crm.service.interfaces.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.List;

@Controller
@PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'OWNER', 'MENTOR')")
@RequestMapping(value = "/status")
public class StatusController {

    private final StatusService statusService;
    private final RoleService roleService;
    private final MessageTemplateService messageTemplateService;

    @Autowired
    public StatusController(
            StatusService statusService,
            RoleService roleService,
            MessageTemplateService messageTemplateService) {
        this.statusService = statusService;
        this.roleService = roleService;
        this.messageTemplateService = messageTemplateService;
    }

    @GetMapping(value = "/{id}")
    public String showStatusClientsSorted(
            Model model,
            @PathVariable("id") String id,
            @AuthenticationPrincipal User userFromSession) {

        List<StatusDtoForBoard> statuses = Collections.emptyList();
        List<Role> sessionRoles = userFromSession.getRole();
        if (sessionRoles.contains(roleService.getRoleByName("OWNER"))) {
            statuses = StatusDtoForBoard.getListDtoStatuses(statusService.getStatusesWithSortedClientsByRole(userFromSession, roleService.getRoleByName("OWNER")));
        }
        if (sessionRoles.contains(roleService.getRoleByName("ADMIN"))
                & !(sessionRoles.contains(roleService.getRoleByName("OWNER")))) {
            statuses = StatusDtoForBoard.getListDtoStatuses(statusService.getStatusesWithSortedClientsByRole(userFromSession, roleService.getRoleByName("ADMIN")));
        }
        if (sessionRoles.contains(roleService.getRoleByName("MENTOR"))
                & !(sessionRoles.contains(roleService.getRoleByName("ADMIN")) || sessionRoles.contains(roleService.getRoleByName("OWNER")))) {
            statuses = StatusDtoForBoard.getListDtoStatuses(statusService.getStatusesWithSortedClientsByRole(userFromSession, roleService.getRoleByName("MENTOR")));
        } else if (sessionRoles.contains(roleService.getRoleByName("USER"))
                & !(sessionRoles.contains(roleService.getRoleByName("MENTOR")) || sessionRoles.contains(roleService.getRoleByName("ADMIN")) || sessionRoles.contains(roleService.getRoleByName("OWNER")))) {
            statuses = StatusDtoForBoard.getListDtoStatuses(statusService.getStatusesWithSortedClientsByRole(userFromSession, roleService.getRoleByName("USER")));
        }

        for (StatusDtoForBoard status : statuses) {
            if (status.getId().equals(Long.parseLong(id))) {
                model.addAttribute("status", status);
                break;
            }
        }

        model.addAttribute("emailTmpl", messageTemplateService.getAll());
        return "fragments/status-client-sorted :: statusClientSorted";
    }
}
