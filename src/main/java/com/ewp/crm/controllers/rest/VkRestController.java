package com.ewp.crm.controllers.rest;

import com.ewp.crm.service.impl.VKService;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.User;
import com.ewp.crm.service.impl.MessageTemplateServiceImpl;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
public class VkRestController {

	private final VKService vkService;
	private final ClientService clientService;
	private final MessageTemplateServiceImpl MessageTemplateService;
	private final UserService userService;

	@Autowired
	public VkRestController(ClientService clientService, MessageTemplateServiceImpl MessageTemplateService, VKService vkService1, UserService userService) {
		this.vkService = vkService1;
		this.clientService = clientService;
		this.MessageTemplateService = MessageTemplateService;
		this.userService = userService;
	}

	@RequestMapping(value = "/rest/vkontakte", method = RequestMethod.POST)
	public ResponseEntity<String> sendToVkontakte(@RequestParam("clientId") Long clientId, @RequestParam("templateId") Long templateId,
												  @RequestParam(value = "body",required = false) String body) {
		User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Client client = clientService.get(clientId);
		String vkText = MessageTemplateService.get(templateId).getOtherText();
		String fullName = client.getName() + " " + client.getLastName();
		Map<String, String> params = new HashMap<>();
		params.put("%fullName%", fullName);
		params.put("%bodyText%", body);

		User user = userService.get(principal.getId());
		String token = user.getVkToken();
		vkService.sendMessageToClient(client, vkText, params, principal, token);
		return ResponseEntity.status(HttpStatus.OK).body("Message send successfully");
	}
}
