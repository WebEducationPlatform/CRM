package com.ewp.crm.controllers;

import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.repository.interfaces.MailingMessageRepository;
import com.ewp.crm.service.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/hr")
@PreAuthorize("hasAnyAuthority('OWNER','HR')")
public class HrController {
	private static Logger logger = LoggerFactory.getLogger(HrController.class);

	private final StatusService statusService;
	private final ClientService clientService;
	private final UserService userService;
	private final MessageTemplateService messageTemplateService;

	private final ProjectPropertiesService propertiesService;

	private final StudentStatusService studentStatus;


	@Value("${project.pagination.page-size.clients}")
	private int pageSize;

	@Autowired
	public HrController(StatusService statusService,
						ClientService clientService,
						UserService userService,
						MessageTemplateService MessageTemplateService,
						ProjectPropertiesService propertiesService,
						StudentStatusService studentStatus) {
		this.statusService = statusService;
		this.clientService = clientService;
		this.userService = userService;
		this.messageTemplateService = MessageTemplateService;
		this.propertiesService = propertiesService;
		this.studentStatus = studentStatus;
	}

	@GetMapping("/students")
	public ModelAndView showAllStudents() {
		ModelAndView modelAndView = new ModelAndView("main-client-table-hr");
		SocialProfile socialProfile = new SocialProfile();
		modelAndView.addObject("allClients", clientService.getAllClientsByPage(PageRequest.of(0, pageSize, Sort.by(Sort.Direction.DESC, "dateOfRegistration"))));
		modelAndView.addObject("statuses", statusService.getAll());
		modelAndView.addObject("users", userService.getAll());
		modelAndView.addObject("socialNetworkTypes", socialProfile.getAllSocialNetworkTypes());
		modelAndView.addObject("projectProperties", propertiesService.get());
		modelAndView.addObject("emailTmpl", messageTemplateService.getAll());
		modelAndView.addObject("studentStatuses", studentStatus.getAll());
		return modelAndView;
	}
}
