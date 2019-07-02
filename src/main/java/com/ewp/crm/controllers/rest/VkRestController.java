package com.ewp.crm.controllers.rest;

import com.ewp.crm.configs.inteface.VKConfig;
import com.ewp.crm.exceptions.member.NotFoundMemberList;
import com.ewp.crm.models.User;
import com.ewp.crm.models.VkMember;
import com.ewp.crm.models.VkTrackedClub;
import com.ewp.crm.service.interfaces.MessageTemplateService;
import com.ewp.crm.service.interfaces.VKService;
import com.ewp.crm.service.interfaces.VkMemberService;
import com.ewp.crm.service.interfaces.VkTrackedClubService;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'MENTOR', 'HR')")
@RequestMapping("/rest/vkontakte")
public class VkRestController {

	private static Logger logger = LoggerFactory.getLogger(VkRestController.class);

	private final VKService vkService;
	private final VkTrackedClubService vkTrackedClubService;
	private final VkMemberService vkMemberService;
	private final MessageTemplateService messageTemplateService;
	private final VKConfig vkConfig;
	private Environment env;

	@Autowired
	public VkRestController(VKService vkService,
							VkTrackedClubService vkTrackedClubService,
							VkMemberService vkMemberService,
							MessageTemplateService messageTemplateService,
							VKConfig vkConfig, Environment env) {
		this.vkService = vkService;
		this.vkTrackedClubService = vkTrackedClubService;
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

    @GetMapping(value = "/trackedclub", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<VkTrackedClub>> getAllTrackedClub() {
        List<VkTrackedClub> vkTrackedClubs = vkTrackedClubService.getAll();
        return ResponseEntity.ok(vkTrackedClubs);
    }

	@PostMapping(value = "/trackedclub/update")
	public ResponseEntity updateVkTrackedClub(@RequestParam Long id,
											  @RequestParam String groupName,
											  @RequestParam String token,
											  @AuthenticationPrincipal User userFromSession) {
		Optional<VkTrackedClub> vkTrackedClub = vkTrackedClubService.get(id);
		if (vkTrackedClub.isPresent()) {
			vkTrackedClub.get().setGroupName(groupName);
			vkTrackedClub.get().setToken(token);
			vkTrackedClubService.update(vkTrackedClub.get());
			logger.info("{} has updated VkTrackedClub: club id {}", userFromSession.getFullName(), vkTrackedClub.get().getGroupId());
			return ResponseEntity.ok(HttpStatus.OK);
		}
		return new ResponseEntity(HttpStatus.NOT_FOUND);
	}

	@PostMapping(value = "/trackedclub/delete")
	public ResponseEntity deleteVkTrackedClub(@RequestParam Long deleteId,
											  @AuthenticationPrincipal User userFromSession) {
		Optional<VkTrackedClub> currentClub = vkTrackedClubService.get(deleteId);
		if (currentClub.isPresent()) {
			vkTrackedClubService.delete(deleteId);
			logger.info("{} has deleted VkTrackedClub: club name {}, id {}", userFromSession.getFullName(),
					currentClub.get().getGroupName(), currentClub.get().getGroupId());
			return ResponseEntity.ok(HttpStatus.OK);
		}
		return new ResponseEntity(HttpStatus.NOT_FOUND);
	}

	@PostMapping(value = "/trackedclub/add")
	public ResponseEntity addVkTrackedClub(@RequestParam String groupId,
										   @RequestParam String groupName,
										   @RequestParam String token,
										   @RequestParam String clientId,
										   @AuthenticationPrincipal User userFromSession) {
		VkTrackedClub newVkClub = new VkTrackedClub(Long.parseLong(groupId),
													token, groupName,
													Long.parseLong(clientId));
		int countNewMembers = 0;
		List<VkMember> memberList = vkMemberService.getAllMembersByGroupId(newVkClub.getGroupId());
		List<VkMember> newMemberList = vkService.getAllVKMembers(newVkClub.getGroupId(), 0L)
				.orElseThrow(() -> new NotFoundMemberList(env.getProperty("messaging.vk.exception.not-found-member-list")));
		if (memberList.isEmpty()) {
			vkMemberService.addAllMembers(newMemberList);

			logger.info("{} has added vkTrackedClub: group id {}, group name {}", userFromSession.getFullName(),
					newVkClub.getGroupId(), newVkClub.getGroupName());
		} else {
			for (VkMember newVkMember : newMemberList) {
				if(!memberList.contains(newVkMember)){
					vkMemberService.add(newVkMember);
					countNewMembers++;
				}
			}
			logger.info("{} has reloaded vkTrackedClub: group id {}, group name {} with {} new VKMembers", userFromSession.getFullName(),
					newVkClub.getGroupId(), newVkClub.getGroupName(), countNewMembers);
		}
		vkTrackedClubService.add(newVkClub);
		return ResponseEntity.ok(HttpStatus.OK);
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
