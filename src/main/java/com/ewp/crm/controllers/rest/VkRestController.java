package com.ewp.crm.controllers.rest;

import com.ewp.crm.exceptions.member.NotFoundMemberList;
import com.ewp.crm.models.VkMember;
import com.ewp.crm.models.VkTrackedClub;
import com.ewp.crm.service.impl.VKService;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.User;
import com.ewp.crm.service.impl.MessageTemplateServiceImpl;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.UserService;
import com.ewp.crm.service.interfaces.VkMemberService;
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
	private final VkTrackedClubService vkTrackedClubService;
	private final VkMemberService vkMemberService;

	@Autowired
	public VkRestController(VKService vkService1, VkTrackedClubService vkTrackedClubService, VkMemberService vkMemberService) {
		this.vkService = vkService1;
		this.vkTrackedClubService = vkTrackedClubService;
		this.vkMemberService = vkMemberService;
	}

	@RequestMapping(value = "/rest/vkontakte", method = RequestMethod.POST)
	public ResponseEntity<String> sendToVkontakte(@RequestParam("clientId") Long clientId, @RequestParam("templateId") Long templateId,
	                                              @RequestParam(value = "body",required = false) String body) {
		User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		vkService.sendMessageToClient(clientId, templateId, body, principal);
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
		VkTrackedClub newVkClub = new VkTrackedClub(Long.parseLong(groupId),
													token, groupName,
													Long.parseLong(clientId));
		int countNewMembers = 0;
		User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<VkMember> memberList = vkMemberService.getAllMembersByGroupId(newVkClub.getGroupId());
		List<VkMember> newMemberList = vkService.getAllVKMembers(newVkClub.getGroupId(), 0L)
				.orElseThrow(NotFoundMemberList::new);
		if (memberList.isEmpty()) {
			vkMemberService.addAllMembers(newMemberList);

			logger.info("{} has added vkTrackedClub: group id {}, group name {}", currentAdmin.getFullName(),
					newVkClub.getGroupId(), newVkClub.getGroupName());
		} else {
			for (VkMember newVkMember : newMemberList) {
				if(!memberList.contains(newVkMember)){
					vkMemberService.add(newVkMember);
					countNewMembers++;
				}
			}
			logger.info("{} has reloaded vkTrackedClub: group id {}, group name {} with {} new VKMembers", currentAdmin.getFullName(),
					newVkClub.getGroupId(), newVkClub.getGroupName(), countNewMembers);
		}
		vkTrackedClubService.add(newVkClub);
		return ResponseEntity.ok(HttpStatus.OK);
	}
}
