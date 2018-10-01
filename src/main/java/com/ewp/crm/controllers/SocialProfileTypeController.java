package com.ewp.crm.controllers;

import com.ewp.crm.models.SocialProfileType;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.NotificationService;
import com.ewp.crm.service.interfaces.SocialProfileTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
public class SocialProfileTypeController {

	private final SocialProfileTypeService socialProfileTypeService;
	private final NotificationService notificationService;

	@Autowired
	public SocialProfileTypeController(SocialProfileTypeService socialProfileTypeService,
									   NotificationService notificationService) {
		this.socialProfileTypeService = socialProfileTypeService;
		this.notificationService = notificationService;
	}

	@GetMapping(value = "/admin/user/socialProfileTypes")
	public ModelAndView socialProfileTypes(ModelAndView modelAndView,
										   @AuthenticationPrincipal User userFromSession) {
		List<SocialProfileType> socialProfileTypes = socialProfileTypeService.getAll();
		modelAndView.addObject("socialProfileTypes", socialProfileTypes);
		modelAndView.setViewName("socialProfileTypes-table");
		modelAndView.addObject("notifications", notificationService.getByUserToNotify(userFromSession));
		return modelAndView;
	}

	@PostMapping(value = "/admin/user/addSocialProfileType")
	public ModelAndView addSocialProfileType(@ModelAttribute SocialProfileType socialProfileType) {
		socialProfileTypeService.add(socialProfileType);
		return new ModelAndView("redirect:/admin/user/socialProfileTypes");
	}
}
