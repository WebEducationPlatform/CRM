package com.ewp.crm.controllers;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Notification;
import com.ewp.crm.models.Role;
import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.models.User;
import com.ewp.crm.models.dto.StatusDtoForBoard;
import com.ewp.crm.repository.interfaces.MailingMessageRepository;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.ListMailingService;
import com.ewp.crm.service.interfaces.ListMailingTypeService;
import com.ewp.crm.service.interfaces.MessageTemplateService;
import com.ewp.crm.service.interfaces.NotificationService;
import com.ewp.crm.service.interfaces.ProjectPropertiesService;
import com.ewp.crm.service.interfaces.RoleService;
import com.ewp.crm.service.interfaces.SlackService;
import com.ewp.crm.service.interfaces.StatusService;
import com.ewp.crm.service.interfaces.StudentStatusService;
import com.ewp.crm.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ClientController {

    private static Logger logger = LoggerFactory.getLogger(ClientController.class);
    private final StatusService statusService;
    private final ClientService clientService;
    private final UserService userService;
    private final MessageTemplateService messageTemplateService;
    private final NotificationService notificationService;
    private final RoleService roleService;
    private final ProjectPropertiesService propertiesService;
    private final ListMailingService listMailingService;
    private final MailingMessageRepository messageService;
    private final StudentStatusService studentStatus;
    private final ListMailingTypeService listMailingTypeService;
    private final SlackService slackService;

    @Value("${project.pagination.page-size.clients}")
    private int pageSize;

    @Autowired
    public ClientController(StatusService statusService,
                            ClientService clientService,
                            UserService userService,
                            MessageTemplateService MessageTemplateService,
                            NotificationService notificationService,
                            RoleService roleService,
                            ProjectPropertiesService propertiesService,
                            ListMailingService listMailingService,
                            MailingMessageRepository messageService,
                            StudentStatusService studentStatus,
                            ListMailingTypeService listMailingTypeService,
                            SlackService slackService) {
        this.slackService = slackService;
        this.statusService = statusService;
        this.clientService = clientService;
        this.userService = userService;
        this.messageTemplateService = MessageTemplateService;
        this.notificationService = notificationService;
        this.roleService = roleService;
        this.propertiesService = propertiesService;
        this.listMailingService = listMailingService;
        this.messageService = messageService;
        this.studentStatus = studentStatus;
        this.listMailingTypeService = listMailingTypeService;
    }

    @GetMapping(value = "/admin/client/add/{statusName}")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'HR')")
    public ModelAndView addClient(@PathVariable String statusName,
                                  @AuthenticationPrincipal User userFromSession) {
        ModelAndView modelAndView = new ModelAndView("add-client");
        statusService.get(statusName).ifPresent(s -> modelAndView.addObject("status", s));
        SocialProfile socialProfile = new SocialProfile();
        modelAndView.addObject("states", Client.State.values());
        modelAndView.addObject("socialMarkers", socialProfile.getAllSocialNetworkTypes());
        modelAndView.addObject("user", userFromSession);
        modelAndView.addObject("notifications", notificationService.getByUserToNotify(userFromSession));
        return modelAndView;
    }

    @GetMapping(value = "/admin/client/add")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'HR')")
    public ModelAndView addClient(@AuthenticationPrincipal User userFromSession) {
        ModelAndView modelAndView = new ModelAndView("add-client");
        SocialProfile socialProfile = new SocialProfile();
        modelAndView.addObject("statuses", statusService.getAll());
        modelAndView.addObject("states", Client.State.values());
        modelAndView.addObject("socialMarkers", socialProfile.getAllSocialNetworkTypes());
        modelAndView.addObject("user", userFromSession);
        modelAndView.addObject("notifications", notificationService.getByUserToNotify(userFromSession));
        return modelAndView;
    }

    @GetMapping(value = "/client")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'OWNER', 'MENTOR', 'HR')")
    public ModelAndView getAll(@AuthenticationPrincipal User userFromSession) {
        List<StatusDtoForBoard> statuses = null;
        ModelAndView modelAndView = null;
        //TODO Сделать ещё адекватней
        List<Role> sessionRoles = userFromSession.getRole();
        if (sessionRoles.contains(roleService.getRoleByName("OWNER"))) {
            statuses = StatusDtoForBoard.getListDtoStatuses(statusService.getStatusesWithSortedClientsByRole(userFromSession, roleService.getRoleByName("OWNER")));
            modelAndView = new ModelAndView("main-client-table");
            modelAndView.addObject("statuses", statuses);
        }
        if (sessionRoles.contains(roleService.getRoleByName("ADMIN"))
                & !(sessionRoles.contains(roleService.getRoleByName("OWNER")))) {
            statuses = StatusDtoForBoard.getListDtoStatuses(statusService.getStatusesWithSortedClientsByRole(userFromSession, roleService.getRoleByName("ADMIN")));
            modelAndView = new ModelAndView("main-client-table");
            modelAndView.addObject("statuses", statuses);
        }
        if (sessionRoles.contains(roleService.getRoleByName("MENTOR"))
                & !(sessionRoles.contains(roleService.getRoleByName("ADMIN")) || sessionRoles.contains(roleService.getRoleByName("OWNER")))) {
            statuses = StatusDtoForBoard.getListDtoStatuses(statusService.getStatusesWithSortedClientsByRole(userFromSession, roleService.getRoleByName("MENTOR")));
            modelAndView = new ModelAndView("main-client-table-mentor");
            modelAndView.addObject("statuses", statuses);
        }
        if (sessionRoles.contains(roleService.getRoleByName("HR"))) {
            statuses = StatusDtoForBoard.getListDtoStatuses(statusService.getStatusesWithSortedClientsByRole(userFromSession, roleService.getRoleByName("HR")));
            modelAndView = new ModelAndView("main-client-table");
            modelAndView.addObject("statuses", statuses);
        }
        else if(sessionRoles.contains(roleService.getRoleByName("USER"))
                & !(sessionRoles.contains(roleService.getRoleByName("MENTOR")) || sessionRoles.contains(roleService.getRoleByName("ADMIN")) || sessionRoles.contains(roleService.getRoleByName("OWNER")))){
            modelAndView = new ModelAndView("main-client-table-user");
            statuses = StatusDtoForBoard.getListDtoStatuses(statusService.getStatusesWithSortedClientsByRole(userFromSession, roleService.getRoleByName("USER")));
            modelAndView.addObject("statuses", statuses);
        }
        List<User> userList = userService.getAll();
        List<Role> roles = roleService.getAll();
        roles.remove(roleService.getRoleByName("OWNER"));
        statuses.sort(Comparator.comparing(StatusDtoForBoard::getPosition));
        // Добавляем список ролей системы, кроме OWNER
        modelAndView.addObject("roles", roles);
        modelAndView.addObject("user", userFromSession);
        modelAndView.addObject("users", userList.stream().filter(User::isVerified).collect(Collectors.toList()));
        modelAndView.addObject("users_without_mentors", userList.stream().filter(x -> !x.getRole().contains(roleService.getRoleByName("MENTOR"))).collect(Collectors.toList()));
        modelAndView.addObject("newUsers", userList.stream().filter(x -> !x.isVerified()).collect(Collectors.toList()));
        modelAndView.addObject("mentors", userList.stream().filter(x -> x.getRole().contains(roleService.getRoleByName("MENTOR"))).collect(Collectors.toList()));
        modelAndView.addObject("emailTmpl", messageTemplateService.getAll());
        modelAndView.addObject("slackWorkspaceUrl", slackService.getSlackWorkspaceUrl());
        if (sessionRoles.contains(roleService.getRoleByName("OWNER")) ||
                sessionRoles.contains(roleService.getRoleByName("ADMIN")) ||
                sessionRoles.contains(roleService.getRoleByName("HR"))) {
            modelAndView.addObject("notifications", notificationService.getByUserToNotify(userFromSession));
            modelAndView.addObject("notifications_type_sms", notificationService.getByUserToNotifyAndType(userFromSession, Notification.Type.SMS));
            modelAndView.addObject("notifications_type_comment", notificationService.getByUserToNotifyAndType(userFromSession, Notification.Type.COMMENT));
            modelAndView.addObject("notifications_type_postpone", notificationService.getByUserToNotifyAndType(userFromSession, Notification.Type.POSTPONE));
            modelAndView.addObject("notifications_type_new_user", notificationService.getByUserToNotifyAndType(userFromSession, Notification.Type.NEW_USER));
        }
        return modelAndView;
    }

    @GetMapping(value = "/client/allClients")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'MENTOR', 'HR')")
    public ModelAndView allClientsPage() {
        ModelAndView modelAndView = new ModelAndView("all-clients-table");
        SocialProfile socialProfile = new SocialProfile();
        modelAndView.addObject("allClients", clientService.getAllClientsByPage(PageRequest.of(0, pageSize, Sort.by(Sort.Direction.DESC, "dateOfRegistration"))));
        modelAndView.addObject("statuses", statusService.getAll());
        modelAndView.addObject("users", userService.getAll());
        modelAndView.addObject("socialNetworkTypes", socialProfile.getAllSocialNetworkTypes());
        modelAndView.addObject("projectProperties", propertiesService.get());
        modelAndView.addObject("emailTmpl", messageTemplateService.getAll());
        modelAndView.addObject("studentStatuses", studentStatus.getAll());
        modelAndView.addObject("slackWorkspaceUrl", slackService.getSlackWorkspaceUrl());
        return modelAndView;
    }

    @GetMapping(value = "/client/mailing")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'MENTOR', 'HR')")
    public ModelAndView mailingPage() {
        ModelAndView modelAndView = new ModelAndView("mailing");
        modelAndView.addObject("listMailing", listMailingService.getAll());
        modelAndView.addObject("chooseUser", userService.getAll());
        modelAndView.addObject("mailingMessage", messageService.findAll());
        modelAndView.addObject("listMailingTypes", listMailingTypeService.getAll());
        return modelAndView;
    }

    @GetMapping(value = "/client/clientInfo/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'OWNER', 'MENTOR', 'HR')")
    public ModelAndView clientInfo(@PathVariable Long id,
                                   @AuthenticationPrincipal User userFromSession) {
        ModelAndView modelAndView = new ModelAndView("client-info");
        SocialProfile socialProfile = new SocialProfile();
        modelAndView.addObject("client", clientService.get(id));
        modelAndView.addObject("statuses", statusService.getAll());
        modelAndView.addObject("states", Client.State.values());
        modelAndView.addObject("socialMarkers", socialProfile.getAllSocialNetworkTypes());
        modelAndView.addObject("user", userFromSession);
        modelAndView.addObject("notifications", notificationService.getByUserToNotify(userFromSession));
        return modelAndView;
    }

    @GetMapping(value = "/phone")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'OWNER', 'MENTOR', 'HR')")
    public ModelAndView getPhone() {
        return new ModelAndView("webrtrc");
    }

}
