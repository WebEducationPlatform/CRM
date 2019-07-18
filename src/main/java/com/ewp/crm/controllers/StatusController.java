package com.ewp.crm.controllers;

import com.ewp.crm.models.Status;
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

import java.util.Optional;

@Controller
@PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'OWNER', 'HR', 'MENTOR')")
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

        Long statusId = Long.parseLong(id);
        Optional<Status> optional = statusService.get(statusId);
        if (!(optional.isPresent())) {
            return "";
        }

        StatusDtoForBoard status = StatusDtoForBoard.getStatusDto(optional.get());
        Optional<Status> statusOptional = statusService.findStatusWithSortedClientsByUser(statusId, userFromSession);
        if (statusOptional.isPresent()) {
            status = StatusDtoForBoard.getStatusDto(statusOptional.get());
        }

        model.addAttribute("status", status);
        model.addAttribute("emailTmpl", messageTemplateService.getAll());

        return "fragments/htmlFragments::clientsForStatus";
    }

}
