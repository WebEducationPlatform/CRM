package com.ewp.crm.controllers;

import com.ewp.crm.service.google.GoogleOAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Controller
public class MainController {

	private  GoogleOAuthService googleOAuthService;
	@Autowired
	public MainController(GoogleOAuthService googleOAuthService) {
		this.googleOAuthService = googleOAuthService;
	}

	@GetMapping(value = {"/", "/login"})
	public ModelAndView homePage() {
		ModelAndView modelAndView;
		if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")) {
			 modelAndView = new ModelAndView("login");
			modelAndView.addObject("GoogleAuthorizationUrl", googleOAuthService.oAuth20Service().getAuthorizationUrl());

		} else {
			 modelAndView =  new ModelAndView("redirect:/client");
		}
		return modelAndView;
	}

	@GetMapping(value = {"/accessDenied"})
	public String accessDenied() {
		return "login";
	}

	//  Authorization OAuth2
	@GetMapping(value = {"/googleoauth2"})
	public String ouath2(
			@RequestParam(required = false) String code,
			Map<String, Object> model) throws InterruptedException, ExecutionException, IOException {

		if (googleOAuthService.GoogleOAuth2(code)) {
			return "redirect:/client";
		} else {
			model.put("message", "Need authorized or Sign in");
			return "login.html";
		}

	}
}
