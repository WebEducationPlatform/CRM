package com.ewp.crm.controllers;

import com.ewp.crm.models.*;
import com.ewp.crm.service.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdminController {

	private static Logger logger = LoggerFactory.getLogger(AdminController.class);

	private final StatusService statusService;
	private final SocialNetworkTypeService socialNetworkTypeService;
	private final NotificationService notificationService;

	@Value("${project.pagination.page-size.clients}")
	private int pageSize;

	@Autowired
	public AdminController(StatusService statusService,
						   SocialNetworkTypeService socialNetworkTypeService,
						   NotificationService notificationService) {
		this.statusService = statusService;
		this.socialNetworkTypeService = socialNetworkTypeService;
		this.notificationService = notificationService;
	}

	@GetMapping(value = "/admin/client/add/{statusName}")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
	public ModelAndView addClient(@PathVariable String statusName,
								  @AuthenticationPrincipal User userFromSession) {
		ModelAndView modelAndView = new ModelAndView("add-client");
		modelAndView.addObject("status", statusService.get(statusName));
		modelAndView.addObject("states", Client.State.values());
		modelAndView.addObject("socialMarkers", socialNetworkTypeService.getAll());
		modelAndView.addObject("user", userFromSession);
		modelAndView.addObject("notifications", notificationService.getByUserToNotify(userFromSession));
		return modelAndView;
	}
}

