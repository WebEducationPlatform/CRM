package com.ewp.crm.controllers;

import com.ewp.crm.configs.ImageConfig;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.NotificationService;
import com.ewp.crm.service.interfaces.RoleService;
import com.ewp.crm.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

	private static Logger logger = LoggerFactory.getLogger(UserController.class);

	private final UserService userService;
	private final RoleService roleService;
	private final ImageConfig imageConfig;
	private final NotificationService notificationService;

	@Autowired
	public UserController(UserService userService,
						  RoleService roleService,
						  ImageConfig imageConfig,
						  NotificationService notificationService) {
		this.userService = userService;
		this.roleService = roleService;
		this.imageConfig = imageConfig;
		this.notificationService = notificationService;
	}

	@GetMapping(value = "/admin/user/{id}")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
	public ModelAndView clientInfo(@PathVariable Long id,
								   @AuthenticationPrincipal User userFromSession) {
		ModelAndView modelAndView = new ModelAndView("user-info");
		modelAndView.addObject("user", userService.get(id));
		modelAndView.addObject("roles", roleService.getAll());
		modelAndView.addObject("maxSize", imageConfig.getMaxImageSize());
		modelAndView.addObject("notifications", notificationService.getByUserToNotify(userFromSession));
		return modelAndView;
	}

	@GetMapping(value = "/admin/user/add")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
	public ModelAndView addUser(@AuthenticationPrincipal User userFromSession) {
		ModelAndView modelAndView = new ModelAndView("add-user");
		modelAndView.addObject("roles", roleService.getAll());
		modelAndView.addObject("maxSize", imageConfig.getMaxImageSize());
		modelAndView.addObject("notifications", notificationService.getByUserToNotify(userFromSession));
		return modelAndView;
	}

	@GetMapping(value = "/user/register")
	public ModelAndView registerUser() {
		ModelAndView modelAndView = new ModelAndView("user-registration");
		modelAndView.addObject("maxSize", imageConfig.getMaxImageSize());
		return modelAndView;
	}

	@GetMapping(value = "/user/customize")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ModelAndView getUserCustomize(@AuthenticationPrincipal User userFromSession) {
		ModelAndView modelAndView = new ModelAndView("user-customize");
		modelAndView.addObject("notifications", notificationService.getByUserToNotify(userFromSession));
		return modelAndView;
	}

	@PostMapping(value = "/user/enableNotifications")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ModelAndView enableNotifications(@RequestParam boolean notifications,
											@AuthenticationPrincipal User userFromSession) {
		userFromSession.setEnableMailNotifications(notifications);
		userService.update(userFromSession);
		return new ModelAndView("redirect:/user/customize");
	}

}
