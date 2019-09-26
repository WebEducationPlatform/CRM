package com.ewp.crm.controllers.rest;

import com.ewp.crm.configs.inteface.VKConfig;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.MessageTemplateService;
import com.ewp.crm.service.interfaces.VKService;
import com.ewp.crm.service.interfaces.VkMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'MENTOR', 'HR')")
@RequestMapping("/rest/vkontakte")
public class VkRestController {

	private static Logger logger = LoggerFactory.getLogger(VkRestController.class);

	private final VKService vkService;
	private final VkMemberService vkMemberService;
	private final MessageTemplateService messageTemplateService;
	private final VKConfig vkConfig;
	private Environment env;

	@Autowired
	public VkRestController(VKService vkService,
							VkMemberService vkMemberService,
							MessageTemplateService messageTemplateService,
							VKConfig vkConfig, Environment env) {
		this.vkService = vkService;
		this.vkMemberService = vkMemberService;
		this.messageTemplateService = messageTemplateService;
		this.vkConfig = vkConfig;
		this.env = env;
	}

    @PostMapping
    public ResponseEntity<String> sendToVkontakte(@RequestParam("clientId") Long clientId,
                                                  @RequestParam("templateId") Long templateId,
                                                  @RequestParam(value = "body",required = false) String body,
												  @AuthenticationPrincipal User userFromSession) {
        String templateText = messageTemplateService.get(templateId).getOtherText();
        vkService.sendMessageToClient(clientId, templateText, body, userFromSession);
        return ResponseEntity.status(HttpStatus.OK).body(env.getProperty("messaging.vk.rest.message-send-ok"));
    }


	@GetMapping(value = "/connectParam")
	public Map<String, String> vkGetAccessToken() {

		Map<String, String> param = new HashMap<>();
		param.put("groupID", vkConfig.getClubId());
		param.put("accessToken", vkConfig.getCommunityToken());
		param.put("version", vkConfig.getVersion());
		param.put("url", vkConfig.getVkApiUrl());

		return param;
	}

	@GetMapping(value = "/getProfilePhotoById", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getProfilePhotoLinkById(@RequestParam String vkref){
		String profilePhotoLink = vkService.getVkPhotoLinkByClientProfileId(vkref);
		return ResponseEntity.ok(profilePhotoLink);
	}

	@GetMapping(value = "/vk-countries", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getCountries() {
        String result = vkService.getAllCountries().get();
		return ResponseEntity.ok(result);
	}

	@GetMapping(value = "/vk-cities", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getCities(@RequestParam String country, String q) {
		int countryId = Integer.parseInt(country);
		String query = q;
		String result = vkService.getCitiesByCountry(countryId, query).get();
		return ResponseEntity.ok(result);
	}

}
