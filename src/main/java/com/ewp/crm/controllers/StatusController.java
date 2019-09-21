package com.ewp.crm.controllers;

import com.ewp.crm.models.MessageTemplate;
import com.ewp.crm.models.Notification;
import com.ewp.crm.models.Role;
import com.ewp.crm.models.Status;
import com.ewp.crm.models.User;
import com.ewp.crm.models.dto.StatusDtoForBoard;
import com.ewp.crm.service.interfaces.MessageTemplateService;
import com.ewp.crm.service.interfaces.NotificationService;
import com.ewp.crm.service.interfaces.RoleService;
import com.ewp.crm.service.interfaces.StatusService;
import com.ewp.crm.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ewp.crm.util.Constants.ROLE_NAME_ADMIN;
import static com.ewp.crm.util.Constants.ROLE_NAME_HR;
import static com.ewp.crm.util.Constants.ROLE_NAME_MENTOR;
import static com.ewp.crm.util.Constants.ROLE_NAME_OWNER;
import static com.ewp.crm.util.Constants.ROLE_NAME_USER;

@Controller
@PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'OWNER', 'HR', 'MENTOR')")
@RequestMapping(value = "/status")
public class StatusController {

    private CachedStatusModelAttributes cachedStatusModelAttributes;

    private final StatusService statusService;
    private final MessageTemplateService messageTemplateService;
    private final NotificationService notificationService;
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public StatusController(
            StatusService statusService,
            MessageTemplateService messageTemplateService, NotificationService notificationService, UserService userService, RoleService roleService) {
        this.statusService = statusService;
        this.messageTemplateService = messageTemplateService;
        this.notificationService = notificationService;
        this.userService = userService;
        this.roleService = roleService;
        this.cachedStatusModelAttributes = new CachedStatusModelAttributes();
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

        model.addAttribute("user", userFromSession);

        model.addAttribute("statuses", cachedStatusModelAttributes.statuses);
        model.addAttribute("roles", cachedStatusModelAttributes.roles);

        model.addAttribute("users", cachedStatusModelAttributes.users);
        model.addAttribute("users_without_mentors", cachedStatusModelAttributes.usersWithoutMentors);
        model.addAttribute("mentors", cachedStatusModelAttributes.mentors);

        model.addAttribute("emailTmpl", cachedStatusModelAttributes.emailTmpl);

        model.addAttribute("notifications", cachedStatusModelAttributes.notifications);
        model.addAttribute("notifications_type_sms", cachedStatusModelAttributes.notificationsTypeSms);
        model.addAttribute("notifications_type_comment", cachedStatusModelAttributes.notificationsTypeComment);
        model.addAttribute("notifications_type_postpone", cachedStatusModelAttributes.notificationsTypePostpone);

        return "fragments/htmlFragments::clientsForStatus";
    }

    public void prepareCachedStatusModelAttributes(User userFromSession) {
        List<Role> sessionRoles = userFromSession.getRole();
        Role role = roleService.getRoleByName(ROLE_NAME_USER);
        if (sessionRoles.contains(roleService.getRoleByName(ROLE_NAME_MENTOR))) {
            role = roleService.getRoleByName(ROLE_NAME_MENTOR);
        }
        if (sessionRoles.contains(roleService.getRoleByName(ROLE_NAME_HR))) {
            role = roleService.getRoleByName(ROLE_NAME_HR);
        }
        if (sessionRoles.contains(roleService.getRoleByName(ROLE_NAME_ADMIN))) {
            role = roleService.getRoleByName(ROLE_NAME_ADMIN);
        }
        if (sessionRoles.contains(roleService.getRoleByName(ROLE_NAME_OWNER))) {
            role = roleService.getRoleByName(ROLE_NAME_OWNER);
        }
        cachedStatusModelAttributes.statuses = statusService.getStatusesForBoardByUserAndRole(userFromSession, role);

        List<Role> roles = roleService.getAll();
        roles.remove(roleService.getRoleByName("OWNER"));
        cachedStatusModelAttributes.roles = roles;

        List<User> userList = userService.getAll();
        cachedStatusModelAttributes.users = userList.stream().filter(User::isVerified).collect(Collectors.toList());
        cachedStatusModelAttributes.usersWithoutMentors = userList.stream()
                .filter(x -> !x.getRole().contains(roleService.getRoleByName("MENTOR")))
                .collect(Collectors.toList());
        cachedStatusModelAttributes.mentors = userList.stream()
                .filter(x -> x.getRole().contains(roleService.getRoleByName("MENTOR")))
                .collect(Collectors.toList());
        cachedStatusModelAttributes.emailTmpl = messageTemplateService.getAll();
        cachedStatusModelAttributes.notifications = notificationService.getByUserToNotify(userFromSession);
        cachedStatusModelAttributes.notificationsTypeSms = notificationService
                .getByUserToNotifyAndType(userFromSession, Notification.Type.SMS);
        cachedStatusModelAttributes.notificationsTypeComment = notificationService
                .getByUserToNotifyAndType(userFromSession, Notification.Type.COMMENT);
        cachedStatusModelAttributes.notificationsTypePostpone = notificationService
                .getByUserToNotifyAndType(userFromSession, Notification.Type.POSTPONE);
    }

    private final class CachedStatusModelAttributes {
        List<StatusDtoForBoard> statuses;
        List<Role> roles;
        List<User> users;
        List<User> usersWithoutMentors;
        List<User> mentors;
        List<MessageTemplate> emailTmpl;
        List<Notification> notifications;
        List<Notification> notificationsTypeSms;
        List<Notification> notificationsTypeComment;
        List<Notification> notificationsTypePostpone;
    }

    //Формирование колонки с одним статусом
    @GetMapping(value = "/get/{id}")
    public String showStatus(
            Model model,
            @PathVariable("id") Long statusId,
            @AuthenticationPrincipal User userFromSession) {

        Optional<Status> optional = statusService.get(statusId);
        if (!(optional.isPresent())) {
            return "";
        }
        model.addAttribute("statuses", optional.get());
        model.addAttribute("emailTmpl", messageTemplateService.getAll());
        return "fragments/list-status::listStatus";
    }

}
