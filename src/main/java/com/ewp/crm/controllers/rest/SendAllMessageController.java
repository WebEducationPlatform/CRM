package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Client;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
public class SendAllMessageController {


	@RequestMapping(value = "/rest/sendSeveralMessage", method = RequestMethod.POST)
	public ResponseEntity sendSeveralMessage(@RequestBody String[] boxList) {
		if (Arrays.asList(boxList).contains("vk")) {
			System.out.println("VKесть!");
		}
		if (Arrays.asList(boxList).contains("facebook")) {
			System.out.println("FBесть!");
		}
		if (Arrays.asList(boxList).contains("email")) {
			System.out.println("EMAILесть!");
		}
		if (Arrays.asList(boxList).contains("sms")) {
			System.out.println("SMSесть!");
		}
		return ResponseEntity.ok(HttpStatus.OK);
	}
}



