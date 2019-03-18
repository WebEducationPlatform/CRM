package com.ewp.crm.controllers;

import com.ewp.crm.service.interfaces.GoogleAuthorizationService;
import com.ewp.crm.service.interfaces.GoogleCalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class GoogleCalendarController {
	private final GoogleCalendarService calendarService;
	private final GoogleAuthorizationService authorizationService;

	@Autowired
	public GoogleCalendarController(GoogleCalendarService calendarService, GoogleAuthorizationService authorizationService) {
		this.calendarService = calendarService;
		this.authorizationService = authorizationService;
	}

	@RequestMapping(value = "/login/google", method = RequestMethod.GET, params = "code")
	public RedirectView oauth2Callback(@RequestParam(value = "code") String code) {
		authorizationService.tokenResponse(code);
		return new RedirectView("/");
	}

	@RequestMapping(value = "/login/google", method = RequestMethod.GET)
	public RedirectView googleConnectionStatus() {
		return new RedirectView(authorizationService.authorize());
	}
}