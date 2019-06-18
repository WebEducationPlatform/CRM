package com.ewp.crm.controllers;

import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.service.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.stream.Collectors;

@Controller
@PreAuthorize("hasAnyAuthority('OWNER','ADMIN','MENTOR')")
@RequestMapping("/mentors")
@PropertySource("file:./slackbot.properties")
public class MentorsController {

	private static Logger logger = LoggerFactory.getLogger(HrController.class);

	private final StatusService statusService;
	private final UserService userService;
	private final MessageTemplateService messageTemplateService;
	private final ProjectPropertiesService propertiesService;
	private final StudentStatusService studentStatus;
	private final RoleService roleService;


	@Autowired
	public MentorsController(StatusService statusService,
							 UserService userService,
							 MessageTemplateService MessageTemplateService,
							 ProjectPropertiesService propertiesService,
							 StudentStatusService studentStatus,
							 RoleService roleService) {
		this.statusService = statusService;
		this.userService = userService;
		this.messageTemplateService = MessageTemplateService;
		this.propertiesService = propertiesService;
		this.studentStatus = studentStatus;
		this.roleService = roleService;
	}

	@Value("${slackbot.ip}")
	private String slackBotIp;
	@Value("${slackbot.port}")
	private String slackBotPort;

	@GetMapping
	public ModelAndView showMentorsWithThearStudents() {
		ModelAndView modelAndView = new ModelAndView("mentors-with-students-table");
		modelAndView.addObject("slackBotIp", slackBotIp);
		modelAndView.addObject("slackBotPort", slackBotPort);
		modelAndView.addObject("mentors", userService.getAll().stream().filter(x -> x.getRole().contains(roleService.getRoleByName("MENTOR"))).collect(Collectors.toList()));
		modelAndView.addObject("studentStatuses", studentStatus.getAll());
		modelAndView.addObject("statuses", statusService.getAll());
		modelAndView.addObject("projectProperties", propertiesService.get());
		modelAndView.addObject("users", userService.getAll());
		modelAndView.addObject("socialNetworkTypes", new SocialProfile().getAllSocialNetworkTypes());
		modelAndView.addObject("emailTmpl", messageTemplateService.getAll());
		return modelAndView;
	}
}
