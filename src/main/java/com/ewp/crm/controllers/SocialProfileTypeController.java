package com.ewp.crm.controllers;

import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.models.SocialProfile.SocialNetworkType;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'HR')")
public class SocialProfileTypeController {


	private final NotificationService notificationService;

	@Autowired
	public SocialProfileTypeController(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@GetMapping(value = "/admin/user/socialProfileTypes")
	public ModelAndView socialProfileTypes(ModelAndView modelAndView,
										   @AuthenticationPrincipal User userFromSession) {
		SocialProfile socialProfile = new SocialProfile();
		List<SocialNetworkType> socialNetworkTypes = socialProfile.getAllSocialNetworkTypes();
		modelAndView.addObject("socialNetworkTypes", socialNetworkTypes);
		modelAndView.setViewName("socialProfileTypes-table");
		modelAndView.addObject("notifications", notificationService.getByUserToNotify(userFromSession));
		return modelAndView;
	}

}
