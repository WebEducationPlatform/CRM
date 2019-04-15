package com.ewp.crm.controllers;

import com.ewp.crm.models.GoogleToken;
import com.ewp.crm.service.interfaces.GoogleAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class GoogleAuthorizationController {

	private final GoogleAuthorizationService authorizationService;

	@Autowired
	public GoogleAuthorizationController(GoogleAuthorizationService authorizationService) {
		this.authorizationService = authorizationService;
	}

	@RequestMapping(value = "/login/google/drive", method = RequestMethod.GET, params = "code")
	public RedirectView oauth2CallbackForDrive(@RequestParam(value = "code") String code) {
		authorizationService.tokenResponse(code, GoogleToken.TokenType.DRIVE);
		return new RedirectView("/");
	}

	@RequestMapping(value = "/login/google/drive", method = RequestMethod.GET)
	public RedirectView googleConnectionStatusForDrive() {
		return new RedirectView(authorizationService.authorize(GoogleToken.TokenType.DRIVE));
	}

	@RequestMapping(value = "/login/google/calendar", method = RequestMethod.GET, params = "code")
	public RedirectView oauth2CallbackForCalendar(@RequestParam(value = "code") String code) {
		authorizationService.tokenResponse(code, GoogleToken.TokenType.CALENDAR);
		return new RedirectView("/");
	}

	@RequestMapping(value = "/login/google/calendar", method = RequestMethod.GET)
	public RedirectView googleConnectionStatusForCalendar() {
		return new RedirectView(authorizationService.authorize(GoogleToken.TokenType.CALENDAR));
	}
}