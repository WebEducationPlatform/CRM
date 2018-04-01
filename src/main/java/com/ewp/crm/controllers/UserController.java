package com.ewp.crm.controllers;

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

	private final UserService userService;
	private final RoleService roleService;
	private final ImageConfig imageConfig;

	@Autowired
	public UserController(UserService userService, RoleService roleService, ImageConfig imageConfig) {
		this.userService = userService;
		this.roleService = roleService;
		this.imageConfig = imageConfig;
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
}
