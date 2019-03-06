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
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.queries.ads.AdsGetBudgetQuery;
import com.vk.api.sdk.queries.ads.AdsGetStatisticsIdsType;
import com.vk.api.sdk.queries.ads.AdsGetStatisticsPeriod;
import com.vk.api.sdk.queries.ads.AdsGetStatisticsQuery;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.env.Environment;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
@RequestMapping("/rest/vkontakte")
public class VkRestController {

	private static Logger logger = LoggerFactory.getLogger(VkRestController.class);

	private final VKService vkService;
	private final VkTrackedClubService vkTrackedClubService;
	private final VkMemberService vkMemberService;
	private final MessageTemplateService messageTemplateService;
	private final VKConfig vkConfig;
	private final RestTemplate restTemplate;
	private String clientId;
	private String clientSecret;
	private String accountId;
	private String serverPort;


	@Autowired
	public VkRestController(VKService vkService,
							VkTrackedClubService vkTrackedClubService,
							VkMemberService vkMemberService,
							MessageTemplateService messageTemplateService,
							VKConfig vkConfig,
	                        Environment environment,
	                        RestTemplate restTemplate) {
		this.vkService = vkService;
		this.vkTrackedClubService = vkTrackedClubService;
		this.vkMemberService = vkMemberService;
		this.messageTemplateService = messageTemplateService;
		this.vkConfig = vkConfig;
		this.clientId = environment.getRequiredProperty("vk.robot.app.clientId");
		this.clientSecret = environment.getRequiredProperty("vk.robot.app.clientSecret");
		this.accountId = environment.getRequiredProperty("vk.accountId");
		this.serverPort = environment.getRequiredProperty("server.port");
		this.restTemplate = restTemplate;


	}

    @PostMapping
    public ResponseEntity<String> sendToVkontakte(@RequestParam("clientId") Long clientId,
                                                  @RequestParam("templateId") Long templateId,
                                                  @RequestParam(value = "body",required = false) String body,
												  @AuthenticationPrincipal User userFromSession) {
        String templateText = messageTemplateService.get(templateId).getOtherText();
        vkService.sendMessageToClient(clientId, templateText, body, userFromSession);
        return ResponseEntity.status(HttpStatus.OK).body("Message send successfully");
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
		VkTrackedClub vkTrackedClub = vkTrackedClubService.get(id);
		vkTrackedClub.setGroupName(groupName);
		vkTrackedClub.setToken(token);
		vkTrackedClubService.update(vkTrackedClub);
		logger.info("{} has updated VkTrackedClub: club id {}", userFromSession.getFullName(), vkTrackedClub.getGroupId());
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@PostMapping(value = "/trackedclub/delete")
	public ResponseEntity deleteVkTrackedClub(@RequestParam Long deleteId,
											  @AuthenticationPrincipal User userFromSession) {
		VkTrackedClub currentClub = vkTrackedClubService.get(deleteId);
		vkTrackedClubService.delete(deleteId);
		logger.info("{} has deleted VkTrackedClub: club name {}, id {}", userFromSession.getFullName(),
																		currentClub.getGroupName(), currentClub.getGroupId());
		return ResponseEntity.ok(HttpStatus.OK);
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
				.orElseThrow(NotFoundMemberList::new);
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
		param.put("url", vkConfig.getVkAPIUrl());

		return param;
	}

	@GetMapping(value = "/getProfilePhotoById", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getProfilePhotoLinkById(@RequestParam String vkref){
		String profilePhotoLink = vkService.getVkPhotoLinkByClientProfileId(vkref);
		return ResponseEntity.ok(profilePhotoLink);
	}

	@GetMapping("/advertisement")
	public String getAdvertisementVk() {
		String url = "https://oauth.vk.com/authorize?" +
				"client_id=" + clientId + "&" +
				"display=page&" +
				"redirect_uri=" + "http://localhost:" + serverPort + "/rest/vkontakte/advertisement/token&" +
				"scope=ads, offline, groups" +
				"response_type=code&v=5.92";

String url1 =  "https://oauth.vk.com/authorize" +
				"?client_id=" + clientId +
				"&display=" + "popup" +
				"&redirect_uri=" + "https://oauth.vk.com/blank.html" +
				"&scope=" + "ads, offline, groups" +
				"&response_type=token" +
				"&v" + "5.78";


		return "redirect:" + url1;
	}

	@GetMapping("/ads")
	public String getToken(@RequestParam String code) throws ClientException, ApiException, JSONException {

	/*	String uriGetMassages = "https://api.vk.com/method/" + "messages.getHistory" +
				"?user_id=" + clubId +
				"&rev=0" +
				"&version=" + version +
				"&access_token=" + technicalAccountToken; */

		TransportClient transportClient = HttpTransportClient.getInstance();
		VkApiClient vk = new VkApiClient(transportClient);
		UserAuthResponse authResponse = vk.oauth()
				.userAuthorizationCodeFlow(Integer.valueOf(clientId), clientSecret, "http://localhost:" + serverPort + "/rest/vkontakte/ads", code)
				.execute();

		UserActor actor = new UserActor(authResponse.getUserId(), authResponse.getAccessToken());
		AdsGetBudgetQuery advertisement = vk.ads().getBudget(actor, Integer.parseInt(accountId));
		String jstring = advertisement.executeAsString();
		JSONObject balance = new JSONObject(advertisement.executeAsString());
		Date date = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat simpleDate = new SimpleDateFormat("dd.MM.yyyy");
		String dateToFrom = simpleDateFormat.format(date);
		AdsGetStatisticsQuery advertisement3 = vk.ads().getStatistics(actor,
				Integer.parseInt(accountId),
				AdsGetStatisticsIdsType.OFFICE,
				accountId,
				AdsGetStatisticsPeriod.DAY,
				dateToFrom,
				dateToFrom);
		String jstring1 = new JSONObject(advertisement3.executeAsString()).toString();
		// JSONObject stats = new JSONObject(advertisement3.executeAsString()).getJSONArray("response").getJSONObject(0).getJSONArray("stats").getJSONObject(0);
		JSONArray stats1 = new JSONObject(advertisement3.executeAsString()).getJSONArray("response").getJSONObject(0).getJSONArray("stats");
		JSONObject stats = new JSONObject(advertisement3.executeAsString()).getJSONArray("response").getJSONObject(0).getJSONArray("stats").getJSONObject(0);

      /*
{
"response": [{
"id": 1605137078,
"type": "office",
"stats": []
}]
}

{"response":[{"stats":[],"id":1605137078,"type":"office"}]}
       */

		Long clicks;
		String monye;

		if (stats.has("clicks")) {
			clicks = stats.getLong("clicks");
		} else {
			clicks = Long.valueOf(0);
		}
		if (stats.has("spent")) {
			monye = stats.getString("spent");
		} else {
			monye = String.valueOf(0);
		}
		String s = "Статистика по вк-рекламному кабинету:\n" +
				"Дата: " + simpleDate.format(date) + "\n" +
				"Количество кликов: " + clicks + "\n" +
				"Денег потрачено: " + monye + "\n" +
				"Баланс: " + balance.getString("response");
		System.out.println(s);
		return "redirect:/client";
	}

	public static void main(String[] args) throws JSONException {
    //      String jstr =  "{\"response\":[{\"stats\":[],\"id\":1605137078,\"type\":\"office\"}]}";
		  String jstr1 = "{\"response\":[{\"id\": 1605137078,\"type\": \"office\",\"stats\": []}]}";
		String jstr2 = "{\"response\":[{\"id\": 1605137078,\"type\": \"office\",\"stats\": [{\"clicks\":\"55\", \"spent\":\"120\"}]}]}";
        JSONArray responce = new JSONObject(jstr2).getJSONArray("response");
        StringBuilder statStr = new StringBuilder("Статистика по вк-рекламному кабинету:\n");
		for (int i = 0; i < responce.length() ; i++) {
			JSONObject item = responce.getJSONObject(i);
			if(item.has("stats")) {
				JSONArray stats = item.getJSONArray("stats");
				for (int j = 0; j < stats.length() ; j++) {
					JSONObject aim = stats.getJSONObject(j);
					if(aim.has("clicks")) {
						statStr.append("Количество кликов: " + aim.getLong("clicks") + "\n");
					}
					if (aim.has("spent")) {
						statStr.append("Денег потрачено: " + aim.getString("spent") + "\n");
					}
				}
			}
		}
		System.out.println(statStr);
	}

}
