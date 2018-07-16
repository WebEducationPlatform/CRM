package com.ewp.crm.component.util;

import com.ewp.crm.configs.inteface.VKConfig;
import com.ewp.crm.exceptions.parse.ParseClientException;
import com.ewp.crm.exceptions.util.VKAccessTokenException;
import com.ewp.crm.models.*;
import com.ewp.crm.service.interfaces.*;
import com.ewp.crm.utils.patterns.ValidationPattern;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;


@Component
public class VKUtil {

	private static Logger logger = LoggerFactory.getLogger(VKUtil.class);
	private String clubId;
	private String version;
	private String communityToken;
	private String applicationId;
	private String display;
	private String redirectUri;
	private String scope;
	private String applicationToken;

	private final String VK_API_METHOD_TEMPLATE = "https://api.vk.com/method/";

	private final SocialNetworkService socialNetworkService;
	private final ClientHistoryService clientHistoryService;
	private final ClientService clientService;
	private final MessageService messageService;
	private final MessageTemplateService messageTemplateService;
	private final SocialNetworkTypeService socialNetworkTypeService;
	private final UserService userService;

	@Autowired
	public VKUtil(VKConfig vkConfig, SocialNetworkService socialNetworkService, ClientHistoryService clientHistoryService, ClientService clientService, MessageService messageService, MessageTemplateService messageTemplateService, SocialNetworkTypeService socialNetworkTypeService, UserService userService) {
		clubId = vkConfig.getClubId();
		version = vkConfig.getVersion();
		communityToken = vkConfig.getCommunityToken();
		applicationId = vkConfig.getApplicationId();
		display = vkConfig.getDisplay();
		redirectUri = vkConfig.getRedirectUri();
		scope = vkConfig.getScope();
		this.socialNetworkService = socialNetworkService;
		this.clientHistoryService = clientHistoryService;
		this.clientService = clientService;
		this.messageService = messageService;
		this.messageTemplateService = messageTemplateService;
		this.socialNetworkTypeService = socialNetworkTypeService;
		this.userService = userService;
	}

	public String receivingTokenUri(){

		return "https://oauth.vk.com/authorize" +
				"?client_id=" + applicationId +
				"&display=" + display +
				"&redirect_uri=" + redirectUri +
				"&scope=" + scope +
				"&response_type=token" +
				"&v" + version;
	}

	public Optional<List<String>> getNewMassages() throws VKAccessTokenException {
		if (applicationToken == null) {
			throw new VKAccessTokenException("VK access token has not got");
		}
		String uriGetMassages = VK_API_METHOD_TEMPLATE + "messages.getHistory" +
				"?user_id=" + clubId +
				"&rev=0" +
				"&version=" + version +
				"&access_token=" + applicationToken;

		String uriMarkAsRead = VK_API_METHOD_TEMPLATE + "messages.markAsRead" +
				"?peer_id=" + clubId +
				"&version=" + version +
				"&access_token=" + applicationToken;
		try {
			HttpGet httpGetMessages = new HttpGet(uriGetMassages);
			HttpGet httpMarkMessages = new HttpGet(uriMarkAsRead);
			HttpClient httpClient = HttpClients.custom()
					.setDefaultRequestConfig(RequestConfig.custom()
							.setCookieSpec(CookieSpecs.STANDARD).build())
					.build();
			HttpResponse response = httpClient.execute(httpGetMessages);
			String result = EntityUtils.toString(response.getEntity());
			JSONObject json = new JSONObject(result);
			JSONArray jsonMessages = json.getJSONArray("response");
			List<String> resultList = new ArrayList<>();
			for (int i = 1; i < jsonMessages.length(); i++) {
				JSONObject jsonMessage = jsonMessages.getJSONObject(i);
				if ((clubId.equals(jsonMessage.getString("uid"))) && (jsonMessage.getInt("read_state") == 0)) {
					resultList.add(jsonMessage.getString("body"));
				}
			}
			httpClient.execute(httpMarkMessages);
			return Optional.of(resultList);
		} catch (JSONException e) {
			logger.error("Can not read message from JSON ", e);
		} catch (IOException e) {
			logger.error("Failed to connect to VK server ", e);
		}
		return Optional.empty();
	}

	public String sendMessageToClient(Long clientId, Long templateId, String body) {
		Client client = clientService.getClientByID(clientId);
		String msg = messageTemplateService.get(templateId).getOtherText();
		String fullName = client.getName() + " " + client.getLastName();
		Map<String, String> params = new HashMap<>();
		params.put("%fullName%", fullName);
		params.put("%bodyText%", body);
		List<SocialNetwork> socialNetworks = socialNetworkService.getAllByClient(client);
		for (SocialNetwork socialNetwork : socialNetworks) {
			if (socialNetwork.getSocialNetworkType().getName().equals("vk")) {
				String link =  validVkLink(socialNetwork.getLink());
				long id = Long.parseLong(link.replaceAll(".+id", ""));
				String vkText = replaceName(msg, params);
				User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				User user = userService.get(principal.getId());
				String token = user.getVk_token();
				String responseMessage = sendMessageById(id, vkText, token);
				Message message = messageService.addMessage(Message.Type.VK, vkText);
				client.addHistory(clientHistoryService.createHistory(principal, client, message));
				clientService.updateClient(client);
				return responseMessage;
			}
		}
		logger.error("{} hasn't vk social network", client.getEmail());
		return client.getName() + " hasn't vk social network";
	}


	public String schedulerSendMessageVk(Long clientId, Long templateId) {
		Client client = clientService.getClientByID(clientId);
		String msg = messageTemplateService.get(templateId).getOtherText();
		String fullName = client.getName() + " " + client.getLastName();
		Map<String, String> params = new HashMap<>();
		params.put("%fullName%", fullName);
		List<SocialNetwork> socialNetworks = socialNetworkService.getAllByClient(client);
		for (SocialNetwork socialNetwork : socialNetworks) {
			if (socialNetwork.getSocialNetworkType().getName().equals("vk")) {
				String link =  validVkLink(socialNetwork.getLink());
				long id = Long.parseLong(link.replaceAll(".+id", ""));
				String vkText = replaceName(msg, params);
				String responseMessage = sendMessageById(id, vkText, communityToken);
				Message message = messageService.addMessage(Message.Type.VK, vkText);
				client.addHistory(clientHistoryService.createHistory(client, message));
				clientService.updateClient(client);
				return responseMessage;
			}
		}
		logger.error("{} hasn't vk social network", client.getEmail());
		return client.getName() + " hasn't vk social network";
	}

	private String sendMessageById(long id, String msg, String token) {
		String replaceCarriage = msg.replaceAll("(\r\n|\n)", "%0A")
                .replaceAll("\"|\'", "%22");
		String uriMsg = replaceCarriage.replaceAll("\\s", "%20");

		String sendMsgRequest = VK_API_METHOD_TEMPLATE + "messages.send" +
				"?user_id=" + id +
				"&v=" + version +
				"&message=" + uriMsg +
				"&access_token=" + token;

		HttpGet request = new HttpGet(sendMsgRequest);
		HttpClient httpClient = HttpClients.custom()
				.setDefaultRequestConfig(RequestConfig.custom()
						.setCookieSpec(CookieSpecs.STANDARD).build())
				.build();
		try {
			HttpResponse response = httpClient.execute(request);
			JSONObject jsonEntity = new JSONObject(EntityUtils.toString(response.getEntity()));
			return determineResponse(jsonEntity);
		} catch (JSONException e) {
			logger.error("JSON couldn't parse response ", e);
		} catch (IOException e) {
			logger.error("Failed connect to vk api ", e);
		}
		return "Failed to send message";
	}

	// Determine text, which varies depending of the success of the sending message
	private String determineResponse(JSONObject jsonObject) throws JSONException {
		try {
			jsonObject.getInt("response");
			return "Message sent";
		} catch (JSONException e) {
			JSONObject jsonError = jsonObject.getJSONObject("error");
			String errorMessage = jsonError.getString("error_msg");
			logger.error(errorMessage);
			return errorMessage;
		}
	}

	public Optional<List<Long>> getUsersIdFromCommunityMessages() {
		String uriGetDialog = VK_API_METHOD_TEMPLATE + "messages.getConversations" +
				"?v=" + version +
				"&filter=unread" +
				"&group_id=" + clubId.replaceAll("-","") +
				"&access_token=" +
				communityToken;

		HttpGet httpGetDialog = new HttpGet(uriGetDialog);
		HttpClient httpClient = HttpClients.custom()
				.setDefaultRequestConfig(RequestConfig.custom()
						.setCookieSpec(CookieSpecs.STANDARD).build())
				.build();
		try {
			HttpResponse response = httpClient.execute(httpGetDialog);
			String result = EntityUtils.toString(response.getEntity());
			JSONObject json = new JSONObject(result);
			JSONObject responseObject = json.getJSONObject("response");
			JSONArray jsonUsers = responseObject.getJSONArray("items");
			List<Long> resultList = new ArrayList<>();
			for (int i = 0; i < jsonUsers.length(); i++) {
				JSONObject jsonMessage = jsonUsers.getJSONObject(i).getJSONObject("last_message");
				resultList.add(jsonMessage.getLong("from_id"));
			}
			return Optional.of(resultList);
		} catch (JSONException e) {
			logger.error("Can not read message from JSON ", e);
		} catch (IOException e) {
			logger.error("Failed to connect to VK server ", e);
		}
		return Optional.empty();
	}

	public Optional<Client> getClientFromVkId(Long id) {
		String uriGetClient = VK_API_METHOD_TEMPLATE + "users.get?" +
				"version=" + version +
				"&user_id=" + id +
				"&access_token=" + applicationToken;

		HttpGet httpGetClient = new HttpGet(uriGetClient);
		HttpClient httpClient = HttpClients.custom()
				.setDefaultRequestConfig(RequestConfig.custom()
						.setCookieSpec(CookieSpecs.STANDARD).build())
				.build();
		try {
			HttpResponse response = httpClient.execute(httpGetClient);
			String result = EntityUtils.toString(response.getEntity());
			JSONObject json = new JSONObject(result);
			JSONArray jsonUsers = json.getJSONArray("response");
			JSONObject jsonUser = jsonUsers.getJSONObject(0);
			String name = jsonUser.getString("first_name");
			String lastName = jsonUser.getString("last_name");
			String vkLink = "vk.com/id" + id;
			Client client = new Client(name, lastName);
			SocialNetwork socialNetwork = new SocialNetwork(vkLink);
			List<SocialNetwork> socialNetworks = new ArrayList<>();
			socialNetworks.add(socialNetwork);
			client.setSocialNetworks(socialNetworks);
			return Optional.of(client);
		} catch (JSONException e) {
			logger.error("Can not read message from JSON ",e);
		} catch (IOException e) {
			logger.error("Failed to connect to VK server ", e);
		}

		return Optional.empty();
	}

	public Client parseClientFromMessage(String message) throws ParseClientException {
		if (!message.startsWith("Новая заявка")) {
			throw new ParseClientException("Invalid message format");
		}
		String[] fields = message.replaceAll("<br>", "").split("Q:");
		Client newClient = new Client();
		try {
			newClient.setName(getValue(fields[1]));
			newClient.setLastName(getValue(fields[2]));
			newClient.setPhoneNumber(getValue(fields[3]));
			newClient.setEmail(getValue(fields[4]).replaceAll("\\s+", ""));
			StringBuilder description = new StringBuilder(getValue(fields[5]));
			if (message.contains("Ваши пожелания по заявке")) {
				description.append(" ");
				description.append(getValue(fields[6]));
			}
			newClient.setClientDescriptionComment(description.toString());
			SocialNetworkType socialNetworkType = socialNetworkTypeService.getByTypeName("vk");
			String social = fields[0];
			SocialNetwork socialNetwork = new SocialNetwork(social.substring(social.indexOf("vk.com/id"), social.indexOf("Диалог")), socialNetworkType);
			newClient.setSocialNetworks(Collections.singletonList(socialNetwork));
		} catch (Exception e) {
			logger.error("Parse error, can't parse income string", e);
		}
		return newClient;
	}

	private static String getValue(String field) {
		return field.substring(field.indexOf("A: ") + 3);
	}

	private String getIdByScreenName(String link) {
		String screenName = link.replaceAll("^.+\\.(com/)", "");
		String request = VK_API_METHOD_TEMPLATE + "utils.resolveScreenName?"
				+ "screen_name=" + screenName
				+ "&access_token=" + applicationToken
				+ "&v=" + version;
		HttpGet httpGetClient = new HttpGet(request);
		HttpClient httpClient = HttpClients.custom()
				.setDefaultRequestConfig(RequestConfig.custom()
						.setCookieSpec(CookieSpecs.STANDARD).build()).build();
		try {
			HttpResponse response = httpClient.execute(httpGetClient);
			String result = EntityUtils.toString(response.getEntity());
			JSONObject json = new JSONObject(result);
			JSONObject responseObject = json.getJSONObject("response");
			String vkId = responseObject.getString("object_id");
			return "https://vk.com/id" + vkId;
		} catch (JSONException e) {
			logger.error("Can't take id by screen name {}", screenName);
		} catch (IOException e) {
			logger.error("Failed to connect to VK server ", e);
		}
		return link;
	}

	private String validVkLink(String link) {
		Pattern pattern = Pattern.compile(ValidationPattern.VK_LINK_PATTERN);
		if (!pattern.matcher(link).matches()) {
			return getIdByScreenName(link);
		}
		return link;
	}

	private String replaceName(String msg, Map<String, String> params) {
		String vkText = msg;
		for (Map.Entry<String, String> entry : params.entrySet()) {
			vkText = String.valueOf(new StringBuilder(vkText.replaceAll(entry.getKey(), entry.getValue())));
		}
		return vkText;
	}

	public void setApplicationToken(String applicationToken) {
        this.applicationToken = applicationToken;
	}

	public String replaceApplicationTokenFromUri(String uri){
        return uri.replaceAll(".+(access_token=)","")
				.replaceAll("&.+","");
	}
}

