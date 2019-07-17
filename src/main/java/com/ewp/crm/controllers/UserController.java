package com.ewp.crm.controllers;

import com.ewp.crm.configs.ImageConfig;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.NotificationService;
import com.ewp.crm.service.interfaces.RoleService;
import com.ewp.crm.service.interfaces.TelegramService;
import com.ewp.crm.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@PropertySource("file:./slackbot.properties")
public class UserController {

	private static Logger logger = LoggerFactory.getLogger(UserController.class);

	private final UserService userService;
	private final RoleService roleService;
	private final ImageConfig imageConfig;
	private final NotificationService notificationService;
	private final TelegramService telegramService;

	@Autowired
	public UserController(UserService userService,
						  RoleService roleService,
						  ImageConfig imageConfig,
						  NotificationService notificationService,
						  TelegramService telegramService) {
		this.userService = userService;
		this.roleService = roleService;
		this.imageConfig = imageConfig;
		this.notificationService = notificationService;
		this.telegramService = telegramService;
	}

	@Value("${slackbot.ip}")
	private String slackBotIp;


	@GetMapping(value = "/admin/user/{id}")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'HR', 'MENTOR')")
	public ModelAndView clientInfo(@PathVariable Long id,
								   @AuthenticationPrincipal User userFromSession) {
		ModelAndView modelAndView = new ModelAndView("user-info");
		modelAndView.addObject("user", userService.get(id));
		modelAndView.addObject("roles", roleService.getAll());
		modelAndView.addObject("maxSize", imageConfig.getMaxImageSize());
		modelAndView.addObject("notifications", notificationService.getByUserToNotify(userFromSession));
		modelAndView.addObject("slackBotIp", slackBotIp);
		return modelAndView;
	}

	@GetMapping(value = "/admin/user/add")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'HR')")
	public ModelAndView addUser(@AuthenticationPrincipal User userFromSession) {
		ModelAndView modelAndView = new ModelAndView("add-user");
		modelAndView.addObject("roles", roleService.getAll());
		modelAndView.addObject("maxSize", imageConfig.getMaxImageSize());
		modelAndView.addObject("notifications", notificationService.getByUserToNotify(userFromSession));
		modelAndView.addObject("slackBotIp", slackBotIp);
		return modelAndView;
	}

	@GetMapping(value = "/user/register")
	public ModelAndView registerUser() {
		ModelAndView modelAndView = new ModelAndView("user-registration");
		modelAndView.addObject("maxSize", imageConfig.getMaxImageSize());
		return modelAndView;
	}

	@GetMapping(value = "/user/customize")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'HR','MENTOR')")
	public ModelAndView getUserCustomize(@AuthenticationPrincipal User userFromSession) {
		ModelAndView modelAndView = new ModelAndView("user-customize");
		modelAndView.addObject("notifications", notificationService.getByUserToNotify(userFromSession));
		modelAndView.addObject("userCustomize", userService.get(userFromSession.getId()));
		modelAndView.addObject("isTelegramAuthenticated", telegramService.isAuthenticated());
		modelAndView.addObject("isTdlibInstalled", telegramService.isTdlibInstalled());
		return modelAndView;
	}

	@PostMapping(value = "/user/autoAnswer")
	@PreAuthorize("hasAnyAuthority('OWNER', 'HR')")
	public ModelAndView changeAutoAnswer(@RequestParam String text,
											@AuthenticationPrincipal User userFromSession) {
	    userFromSession.setAutoAnswer(text);
		userService.update(userFromSession);
		return new ModelAndView("redirect:/user/customize");
	}
	@GetMapping(value = "/user/autoAnswer")
	@PreAuthorize("hasAnyAuthority('OWNER', 'HR')")
	public ModelAndView getAutoAnswerView(@AuthenticationPrincipal User userFromSession) {
		ModelAndView modelAndView = new ModelAndView("user-autoanswer");
		modelAndView.addObject("userCustomize",userFromSession);
		return modelAndView;
	}

}
