package com.ewp.crm.controllers;

import com.ewp.crm.models.*;
import com.ewp.crm.service.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ClientController {

	private static Logger logger = LoggerFactory.getLogger(AdminController.class);

	private final StatusService statusService;
	private final ClientService clientService;
	private final UserService userService;
	private final MessageTemplateService MessageTemplateService;
	private final SocialNetworkTypeService socialNetworkTypeService;
	private final NotificationService notificationService;
	private final RoleService roleService;

	@Value("${project.pagination.page-size.clients}")
	private int pageSize;

	@Autowired
	public ClientController(StatusService statusService,
							ClientService clientService,
							UserService userService,
	                        MessageTemplateService MessageTemplateService,
							SocialNetworkTypeService socialNetworkTypeService,
							NotificationService notificationService,
							RoleService roleService) {
		this.statusService = statusService;
		this.clientService = clientService;
		this.userService = userService;
		this.MessageTemplateService = MessageTemplateService;
		this.socialNetworkTypeService = socialNetworkTypeService;
		this.notificationService = notificationService;
		this.roleService = roleService;
	}

	@GetMapping(value = "/client")
	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'OWNER')")
	public ModelAndView getAll(@AuthenticationPrincipal User userFromSession) {
		List<Status> statuses;
		ModelAndView modelAndView;
		//TODO Сделать ещё адекватней
		if (userFromSession.getRole().contains(roleService.getByRoleName("ADMIN")) || userFromSession.getRole().contains(roleService.getByRoleName("OWNER"))) {
			statuses = statusService.getAll();
			modelAndView = new ModelAndView("main-client-table");
		} else {
			statuses = statusService.getStatusesWithClientsForUser(userFromSession);
			modelAndView = new ModelAndView("main-client-table-user");
		}
		List<User> userList = userService.getAll();
		statuses.sort(Comparator.comparing(Status::getPosition));
		modelAndView.addObject("user", userFromSession);
		modelAndView.addObject("statuses", statuses);
		modelAndView.addObject("users", userList.stream().filter(User::isVerified).collect(Collectors.toList()));
		modelAndView.addObject("newUsers", userList.stream().filter(x -> !x.isVerified()).collect(Collectors.toList()));
		modelAndView.addObject("notifications", notificationService.getByUserToNotify(userFromSession));
		modelAndView.addObject("notifications_type_sms", notificationService.getByUserToNotifyAndType(userFromSession, Notification.Type.SMS));
		modelAndView.addObject("notifications_type_comment", notificationService.getByUserToNotifyAndType(userFromSession, Notification.Type.COMMENT));
		modelAndView.addObject("notifications_type_postpone", notificationService.getByUserToNotifyAndType(userFromSession, Notification.Type.POSTPONE));
		modelAndView.addObject("notifications_type_new_user",notificationService.getByUserToNotifyAndType(userFromSession, Notification.Type.NEW_USER));
		modelAndView.addObject("emailTmpl", MessageTemplateService.getAll());
		return modelAndView;
	}

	@GetMapping(value = "/client/allClients")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ModelAndView allClientsPage() {
		ModelAndView modelAndView = new ModelAndView("all-clients-table");
		modelAndView.addObject("allClients", clientService.findAllByPage(new PageRequest(0, pageSize)));
		modelAndView.addObject("statuses", statusService.getAll());
		modelAndView.addObject("socialNetworkTypes", socialNetworkTypeService.getAll());
		return modelAndView;
	}

	@GetMapping(value = "/client/mailing")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ModelAndView mailingPage() {
		return new ModelAndView("mailing");
	}

	@GetMapping(value = "/client/clientInfo/{id}")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ModelAndView clientInfo(@PathVariable Long id,
								   @AuthenticationPrincipal User userFromSession) {
		ModelAndView modelAndView = new ModelAndView("client-info");
		modelAndView.addObject("client", clientService.get(id));
		modelAndView.addObject("states", Client.State.values());
		modelAndView.addObject("socialMarkers", socialNetworkTypeService.getAll());
		modelAndView.addObject("user", userFromSession);
		modelAndView.addObject("notifications", notificationService.getByUserToNotify(userFromSession));

		return modelAndView;
	}

	@GetMapping(value = "/phone")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ModelAndView getPhone() {
		return new ModelAndView("webrtrc");
	}
}
