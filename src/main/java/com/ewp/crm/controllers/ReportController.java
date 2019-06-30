package com.ewp.crm.controllers;

import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.IPService;
import com.ewp.crm.service.interfaces.MessageTemplateService;
import com.ewp.crm.service.interfaces.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
public class ReportController {

	private final StatusService statusService;
	private final MessageTemplateService messageTemplateService;
	private final IPService ipService;

	@Autowired
	public ReportController(StatusService statusService, MessageTemplateService messageTemplateService, IPService ipService) {
		this.statusService = statusService;
		this.messageTemplateService = messageTemplateService;
		this.ipService = ipService;
	}

	@GetMapping(value = "/report")
	public ModelAndView trackingGroupInfo(@AuthenticationPrincipal User userFromSession) {
		ModelAndView modelAndView = new ModelAndView("report-clients");
		modelAndView.addObject("statuses", statusService.getAll());
		modelAndView.addObject("currentMail", userFromSession.getEmail());
		modelAndView.addObject("emailTmpl", messageTemplateService.getAll());
		modelAndView.addObject("voximplantBalance", ipService.getBalanceOfVoximplantAccountInfo());
		return modelAndView;
	}
}
