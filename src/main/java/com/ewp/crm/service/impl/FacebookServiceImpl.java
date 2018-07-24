package com.ewp.crm.service.impl;

import com.ewp.crm.configs.inteface.FacebookConfig;
import com.ewp.crm.exceptions.util.FBAccessTokenException;
import com.ewp.crm.models.FacebookMessage;
import com.ewp.crm.models.MessageDialog;
import com.ewp.crm.service.interfaces.FacebookDialogService;
import com.ewp.crm.service.interfaces.FacebookService;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FacebookServiceImpl implements FacebookService {

	private static Logger logger = LoggerFactory.getLogger(FacebookServiceImpl.class);

	private String version;
	private String pageToken;
	private String pageId;
	private final RestTemplate restTemplate;
	private FacebookMessageServiceImpl facebookMessageService;
	private FacebookDialogService facebookDialogService;

	@Autowired
	public FacebookServiceImpl(FacebookConfig facebookConfig, RestTemplate restTemplate, FacebookMessageServiceImpl facebookService, FacebookDialogService facebookDialogService) {
		this.version = facebookConfig.getVersion();
		this.pageToken = facebookConfig.getPageToken();
		this.pageId = facebookConfig.getPageId();
		this.restTemplate = restTemplate;
		this.facebookMessageService = facebookService;
		this.facebookDialogService = facebookDialogService;
	}

	private final String FB_API_METHOD_TEMPLATE = "https://graph.facebook.com/";

	public void getFacebookMessages() throws FBAccessTokenException {
		if (pageToken == null) {
			throw new FBAccessTokenException("Facebook access token has not got");
		}
		URI uri = URI.create(FB_API_METHOD_TEMPLATE + version + "/" + pageId + "/" +
				"?fields=about,conversations%7Bmessages%7Bmessage,created_time,from,to%7D%7D" +
				"&access_token=" + pageToken);
		try {
			Date lastMessageDate = facebookMessageService.findMaxDate();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
			ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, null, String.class);
			JSONObject json = new JSONObject(result.getBody());
			JSONObject conversations = (JSONObject) json.get("conversations");
			JSONArray jsonData = conversations.getJSONArray("data");
			JSONArray nestedDatajsonjMessages;
			int count = 0;
			List<FacebookMessage> listMessages = new ArrayList<>();
			while (count != jsonData.length()) {
				JSONObject jsonDataObj = jsonData.getJSONObject(count);
				JSONObject jsonDataObjMessages = jsonDataObj.getJSONObject("messages");
				MessageDialog messageDialog = facebookDialogService.findByDialogId(jsonDataObj.getString("id"));
				if (messageDialog == null) {
					messageDialog = new MessageDialog();
					messageDialog.setDialogId(jsonDataObj.getString("id"));
					nestedDatajsonjMessages = jsonDataObjMessages.getJSONArray("data");
				} else {
					messageDialog.setDialogId(jsonDataObj.getString("id"));
					nestedDatajsonjMessages = jsonDataObjMessages.getJSONArray("data");
				}
				count++;
				for (int i = nestedDatajsonjMessages.length() - 1; i >= 0; i--) {
					JSONObject jsonMessage = nestedDatajsonjMessages.getJSONObject(i);
					String createdTime = jsonMessage.getString("created_time");
					Date date = sdf.parse(createdTime);
					if (lastMessageDate == null || date.after(lastMessageDate)) {
						FacebookMessage facebookMessage = new FacebookMessage();
						facebookMessage.setCreatedTime(date);
						facebookMessage.setTextMessage(jsonMessage.getString("message"));
						facebookMessage.setFrom(jsonMessage.getJSONObject("from").getString("name"));
						facebookMessage.setTo(jsonMessage.getJSONObject("to").getJSONArray("data").getJSONObject(0).getString("name"));
						facebookMessage.setMessagesDialog(messageDialog);
						listMessages.add(facebookMessage);
					}
				}
				facebookDialogService.addDialog(messageDialog);
			}
			facebookMessageService.addBatchMessages(listMessages);
			logger.info("All Facebook messages add to database");
		} catch (ParseException | JSONException e) {
			logger.info("Can't parse Facebook messages", e);
		}
	}

	public String refactorAndValidFbLink(String link){
		String userName = link.replaceAll("^.+\\.(com/)", "");
		String request = FB_API_METHOD_TEMPLATE + version + "/" + userName +
				"&access_token=" + pageToken;
		HttpGet httpGetClient = new HttpGet(request);
		HttpClient httpClient = HttpClients.custom()
				.setDefaultRequestConfig(RequestConfig.custom()
						.setCookieSpec(CookieSpecs.STANDARD).build()).build();
		try {
			HttpResponse response = httpClient.execute(httpGetClient);
			String result = EntityUtils.toString(response.getEntity());
			JSONObject json = new JSONObject(result);
			String fbId = json.getString("id");
//			if (vkUserJson.has("deactivated")){
//				logger.error("VkUser with id {} don't validate", fbId);
//				return "undefined";
//			}
			return "https://www.facebook.com/" + fbId;
		} catch (JSONException e) {
//			logger.error("Can't take id by screen name {}", );
			return "undefined";
		} catch (IOException e) {
			logger.error("Failed to connect to VK server ", e);
			return link;
		}
	}
}

