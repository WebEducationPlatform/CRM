package com.ewp.crm.controllers;

import com.ewp.crm.service.interfaces.SMSInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/user")
public class MessageInfoController {

	private final SMSInfoService smsInfoService;

	@Autowired
	public MessageInfoController(SMSInfoService smsInfoService) {
		this.smsInfoService = smsInfoService;
	}

	@GetMapping("/info/sms/{smsInfoId}")
	public ModelAndView showSmsInfo(@PathVariable("smsInfoId") long id) {
		ModelAndView modelAndView = new ModelAndView("blank_sms-info");
		modelAndView.addObject("smsInfo", smsInfoService.getById(id));
		return modelAndView;
	}

//	@GetMapping("/info/vk/{vkInfoId}")
//	public ModelAndView showVkInfo(@PathVariable("vkInfoId") long id) {
//
//	}
}
