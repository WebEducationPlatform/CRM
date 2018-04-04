package com.ewp.crm.controllers;

import com.ewp.crm.models.*;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.SocialNetworkTypeService;
import com.ewp.crm.service.interfaces.StatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.*;
import java.util.List;

import com.ewp.crm.configs.ImageConfig;
import com.ewp.crm.service.interfaces.RoleService;
import com.ewp.crm.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/user")
public class UserController {

	private static Logger logger = LoggerFactory.getLogger(UserController.class);

	private final UserService userService;
	private final RoleService roleService;
	private final ImageConfig imageConfig;
	private final SocialNetworkTypeService socialNetworkTypeService;

	@Autowired

	public UserController(UserService userService, RoleService roleService, ImageConfig imageConfig, SocialNetworkTypeService socialNetworkTypeService) {
		this.userService = userService;
		this.roleService = roleService;
		this.imageConfig = imageConfig;
		this.socialNetworkTypeService = socialNetworkTypeService;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ModelAndView clientInfo(@PathVariable Long id) {
		ModelAndView modelAndView = new ModelAndView("user-info");
		modelAndView.addObject("user", userService.get(id));
		modelAndView.addObject("roles", roleService.getAll());
		modelAndView.addObject("maxSize", imageConfig.getMaxImageSize());
		return modelAndView;
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public ModelAndView addUser() {
		ModelAndView modelAndView = new ModelAndView("add-user");
		modelAndView.addObject("roles", roleService.getAll());
		modelAndView.addObject("maxSize", imageConfig.getMaxImageSize());
		return modelAndView;
	}

	@RequestMapping(value = "/socialNetworkTypes", method = RequestMethod.GET)
	public ModelAndView socialNetworkTypes(ModelAndView modelAndView) {
		List<SocialNetworkType> socialNetworkTypes = socialNetworkTypeService.getAll();
		modelAndView.addObject("socialNetworkTypes", socialNetworkTypes);
		modelAndView.setViewName("socialNetworkTypes-table");
		return modelAndView;
	}

	@RequestMapping(value = "/deleteSocialNetworkType", method = RequestMethod.GET)
	public ModelAndView deleteSocialNetworkType(@RequestParam String id) {
		socialNetworkTypeService.deleteType(Long.parseLong(id));
		return new ModelAndView("redirect:/admin/user/socialNetworkTypes");

	}

	@RequestMapping(value = "/addSocialNetworkType", method = RequestMethod.POST)
	public ModelAndView addSocialNetworkType(@ModelAttribute SocialNetworkType socialNetworkType) {
		socialNetworkTypeService.addType(socialNetworkType);
		return new ModelAndView("redirect:/admin/user/socialNetworkTypes");
	}

	@RequestMapping(value = "/updateSocialNetworkType", method = RequestMethod.POST)
	public ModelAndView updateSocialNetworkType(@ModelAttribute SocialNetworkType socialNetworkType) {
		socialNetworkTypeService.updateType(socialNetworkType);
		return new ModelAndView("redirect:/admin/user/socialNetworkTypes");
	}


}
