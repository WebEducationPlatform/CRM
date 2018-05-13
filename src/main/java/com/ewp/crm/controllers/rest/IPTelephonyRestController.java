package com.ewp.crm.controllers.rest;


import com.ewp.crm.component.util.interfaces.IPUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/rest/call")
public class IPTelephonyRestController {

	private final IPUtil ipUtil;

	@Autowired
	public IPTelephonyRestController(IPUtil ipUtil) {
		this.ipUtil = ipUtil;
	}

	@RequestMapping(value = "/voximplant", method = RequestMethod.POST)
	public void voximplantCall(@RequestParam String from, @RequestParam String to) {
		//TODO добавить историю
		ipUtil.call(from, to);
	}
}
