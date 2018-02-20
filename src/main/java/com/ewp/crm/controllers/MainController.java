package com.ewp.crm.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MainController {

	@RequestMapping(value = {"/", "/login"}, method = RequestMethod.GET)
	public String homePage() {
		return "login";
	}
}
