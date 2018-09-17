package com.ewp.crm.controllers;

import com.ewp.crm.models.SocialNetworkType;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.NotificationService;
import com.ewp.crm.service.interfaces.SocialNetworkTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
public class SocialNetworkTypeController {

	private final SocialNetworkTypeService socialNetworkTypeService;
	private final NotificationService notificationService;

	@Autowired
	public SocialNetworkTypeController(SocialNetworkTypeService socialNetworkTypeService,
									   NotificationService notificationService) {
		this.socialNetworkTypeService = socialNetworkTypeService;
		this.notificationService = notificationService;
	}

	@GetMapping(value = "/admin/user/socialNetworkTypes")
	public ModelAndView socialNetworkTypes(ModelAndView modelAndView,
										   @AuthenticationPrincipal User userFromSession) {
		List<SocialNetworkType> socialNetworkTypes = socialNetworkTypeService.getAll();
		modelAndView.addObject("socialNetworkTypes", socialNetworkTypes);
		modelAndView.setViewName("socialNetworkTypes-table");
		modelAndView.addObject("notifications", notificationService.getByUserToNotify(userFromSession));
		return modelAndView;
	}

	@PostMapping(value = "/admin/user/addSocialNetworkType")
	public ModelAndView addSocialNetworkType(@ModelAttribute SocialNetworkType socialNetworkType) {
		socialNetworkTypeService.add(socialNetworkType);
		return new ModelAndView("redirect:/admin/user/socialNetworkTypes");
	}
}
