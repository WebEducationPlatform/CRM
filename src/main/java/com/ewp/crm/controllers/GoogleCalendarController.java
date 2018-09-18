package com.ewp.crm.controllers;

import com.ewp.crm.models.Role;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.*;
import com.google.api.services.calendar.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class GoogleCalendarController {

	private static Logger logger = LoggerFactory.getLogger(GoogleCalendarController.class);

	private final StatusService statusService;

	private final ClientService clientService;

	private final UserService userService;

	private final com.ewp.crm.service.interfaces.MessageTemplateService MessageTemplateService;

	private final SocialNetworkTypeService socialNetworkTypeService;

	private final NotificationService notificationService;

	private final RoleService roleService;

	private final GoogleCalendarService calendarService;

	@Autowired
	public GoogleCalendarController(StatusService statusService, ClientService clientService, UserService userService,
									com.ewp.crm.service.interfaces.MessageTemplateService MessageTemplateService, SocialNetworkTypeService socialNetworkTypeService, NotificationService notificationService, ClientHistoryService clientHistoryService, RoleService roleService, GoogleCalendarService calendarService) {
		this.statusService = statusService;
		this.clientService = clientService;
		this.userService = userService;
		this.MessageTemplateService = MessageTemplateService;
		this.socialNetworkTypeService = socialNetworkTypeService;
		this.notificationService = notificationService;
		this.roleService = roleService;
		this.calendarService = calendarService;
	}

	@RequestMapping(value = "/login/google", method = RequestMethod.GET, params = "code")
	public RedirectView oauth2Callback(@RequestParam(value = "code") String code) {
		calendarService.tokenResponse(code);
		return new RedirectView("/");
	}

	@RequestMapping(value = "/login/google", method = RequestMethod.GET)
	public RedirectView googleConnectionStatus(HttpServletRequest request) throws Exception {
		return new RedirectView(calendarService.authorize());
	}
}