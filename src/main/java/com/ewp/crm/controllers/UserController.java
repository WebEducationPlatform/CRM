package com.ewp.crm.controllers;

import com.ewp.crm.models.*;
import com.ewp.crm.service.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import com.ewp.crm.configs.ImageConfig;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserController {

	private static Logger logger = LoggerFactory.getLogger(UserController.class);

	private final UserService userService;
	private final RoleService roleService;
	private final ImageConfig imageConfig;
	private final SocialNetworkTypeService socialNetworkTypeService;
	private final NotificationService notificationService;

	@Autowired

	public UserController(UserService userService, RoleService roleService, ImageConfig imageConfig, SocialNetworkTypeService socialNetworkTypeService, NotificationService notificationService) {
		this.userService = userService;
		this.roleService = roleService;
		this.imageConfig = imageConfig;
		this.socialNetworkTypeService = socialNetworkTypeService;
		this.notificationService = notificationService;
	}

	@RequestMapping(value = "/admin/user/{id}", method = RequestMethod.GET)
	public ModelAndView clientInfo(@PathVariable Long id) {
		ModelAndView modelAndView = new ModelAndView("user-info");
		modelAndView.addObject("user", userService.get(id));
		modelAndView.addObject("roles", roleService.getAll());
		modelAndView.addObject("maxSize", imageConfig.getMaxImageSize());
		User userFromSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		modelAndView.addObject("notifications", notificationService.getNotificationsByUserToNotify(userFromSession));
		return modelAndView;
	}

	@RequestMapping(value = "/admin/user/add", method = RequestMethod.GET)
	public ModelAndView addUser() {
		ModelAndView modelAndView = new ModelAndView("add-user");
		modelAndView.addObject("roles", roleService.getAll());
		modelAndView.addObject("maxSize", imageConfig.getMaxImageSize());
		User userFromSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		modelAndView.addObject("notifications", notificationService.getNotificationsByUserToNotify(userFromSession));
		return modelAndView;
	}

	@RequestMapping(value = "/user/customize", method = RequestMethod.GET)
	public ModelAndView getUserCustomize() {
		ModelAndView modelAndView = new ModelAndView("user-customize");
		User userFromSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		modelAndView.addObject("notifications", notificationService.getNotificationsByUserToNotify(userFromSession));
		return modelAndView;
	}

	@RequestMapping(value = "/user/enableNotifications", method = RequestMethod.POST)
	public ModelAndView enableNotifications(@RequestParam boolean notifications) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		user.setEnableMailNotifications(notifications);
		userService.update(user);
		return new ModelAndView("redirect:/user/customize");
	}

	@RequestMapping(value = "/admin/user/socialNetworkTypes", method = RequestMethod.GET)
	public ModelAndView socialNetworkTypes(ModelAndView modelAndView) {
		List<SocialNetworkType> socialNetworkTypes = socialNetworkTypeService.getAll();
		modelAndView.addObject("socialNetworkTypes", socialNetworkTypes);
		modelAndView.setViewName("socialNetworkTypes-table");
		User userFromSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		modelAndView.addObject("notifications", notificationService.getNotificationsByUserToNotify(userFromSession));
		return modelAndView;
	}

	@RequestMapping(value = "/admin/user/deleteSocialNetworkType", method = RequestMethod.GET)
	public ModelAndView deleteSocialNetworkType(@RequestParam String id) {
		socialNetworkTypeService.deleteType(Long.parseLong(id));
		return new ModelAndView("redirect:/admin/user/socialNetworkTypes");
	}

	@RequestMapping(value = "/admin/user/addSocialNetworkType", method = RequestMethod.POST)
	public ModelAndView addSocialNetworkType(@ModelAttribute SocialNetworkType socialNetworkType) {
		socialNetworkTypeService.addType(socialNetworkType);
		return new ModelAndView("redirect:/admin/user/socialNetworkTypes");
	}

	@RequestMapping(value = "/admin/user/updateSocialNetworkType", method = RequestMethod.POST)
	public ModelAndView updateSocialNetworkType(@ModelAttribute SocialNetworkType socialNetworkType) {
		socialNetworkTypeService.updateType(socialNetworkType);
		return new ModelAndView("redirect:/admin/user/socialNetworkTypes");
	}


}
