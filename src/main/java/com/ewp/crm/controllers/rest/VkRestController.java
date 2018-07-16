package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.VkTrackedClub;
import com.ewp.crm.service.impl.VKService;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.User;
import com.ewp.crm.service.impl.MessageTemplateServiceImpl;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.UserService;
import com.ewp.crm.service.interfaces.VkTrackedClubService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
public class VkRestController {

	private static Logger logger = LoggerFactory.getLogger(VkRestController.class);

	private final VKService vkService;
	private final ClientService clientService;
	private final MessageTemplateServiceImpl MessageTemplateService;
	private final UserService userService;
	private final VkTrackedClubService vkTrackedClubService;

	@Autowired
	public VkRestController(ClientService clientService, MessageTemplateServiceImpl MessageTemplateService, VKService vkService1, UserService userService, VkTrackedClubService vkTrackedClubService) {
		this.vkService = vkService1;
		this.clientService = clientService;
		this.MessageTemplateService = MessageTemplateService;
		this.userService = userService;
		this.vkTrackedClubService = vkTrackedClubService;
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

	@RequestMapping(value = "/rest/vkontakte/trackedclub", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<VkTrackedClub>> getAllTrackedClub() {
		List<VkTrackedClub> vkTrackedClubs = vkTrackedClubService.getAll();
		return ResponseEntity.ok(vkTrackedClubs);
	}

	@RequestMapping(value = "/rest/vkontakte/trackedclub/update", method = RequestMethod.POST)
	public ResponseEntity updateVkTrackedClub(@RequestParam Long id,
											  @RequestParam String groupName,
											  @RequestParam String token) {
		User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		VkTrackedClub vkTrackedClub = vkTrackedClubService.get(id);
		vkTrackedClub.setGroupName(groupName);
		vkTrackedClub.setToken(token);
		vkTrackedClubService.update(vkTrackedClub);
		logger.info("{} has updated VkTrackedClub: club id {}", currentAdmin.getFullName(), vkTrackedClub.getGroupId());
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@RequestMapping(value = "/rest/vkontakte/trackedclub/delete", method = RequestMethod.POST)
	public ResponseEntity deleteVkTrackedClub(@RequestParam Long deleteId) {
		User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		VkTrackedClub currentClub = vkTrackedClubService.get(deleteId);
		vkTrackedClubService.delete(deleteId);
		logger.info("{} has deleted VkTrackedClub: club name {}, id {}", currentAdmin.getFullName(),
																		currentClub.getGroupName(), currentClub.getGroupId());
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@RequestMapping(value = "/rest/vkontakte/trackedclub/add", method = RequestMethod.PUT)
	public ResponseEntity addVkTrackedClub(@RequestParam String groupId,
										   @RequestParam String groupName,
										   @RequestParam String token,
										   @RequestParam String clientId) {
		VkTrackedClub newVkclub = new VkTrackedClub(Long.parseLong(groupId),
													token, groupName,
													Long.parseLong(clientId));
		User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		vkTrackedClubService.add(newVkclub);
		logger.info("{} has added vkTrackedClub: group id {}, group name {}", currentAdmin.getFullName(),
					newVkclub.getGroupId(), newVkclub.getGroupName());
		return ResponseEntity.ok(HttpStatus.OK);
	}
}