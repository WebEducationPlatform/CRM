package com.ewp.crm.service.impl;

import com.ewp.crm.configs.inteface.FacebookConfig;
import com.ewp.crm.exceptions.util.FBAccessTokenException;
import com.ewp.crm.service.interfaces.FacebookService;
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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FacebookServiceImpl implements FacebookService {

	private static Logger logger = LoggerFactory.getLogger(FacebookServiceImpl.class);

	private String version;
	private String pageToken;
	private String pageId;
	private final RestTemplate restTemplate;

	@Autowired
	public FacebookServiceImpl(FacebookConfig facebookConfig, RestTemplate restTemplate) {
		this.version = facebookConfig.getVersion();
		this.pageToken = facebookConfig.getPageToken();
		this.pageId = facebookConfig.getPageId();
		this.restTemplate = restTemplate;
	}

	private final String FB_API_METHOD_TEMPLATE = "https://graph.facebook.com/";

	public Optional<List<String>> getFacebookMessages() throws FBAccessTokenException {
		if (pageToken == null) {
			throw new FBAccessTokenException("Facebook access token has not got");
		}
		URI uri = URI.create(FB_API_METHOD_TEMPLATE  + version + "/" + pageId + "/" +
				"?fields=about,conversations%7Bmessages%7Bfrom,message%7D%7D" +
				"&access_token=" + pageToken);
		try {
			ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, null, String.class);
			JSONObject json = new JSONObject(result.getBody());
			JSONObject conversations = (JSONObject) json.get("conversations");
			JSONArray jsonData = conversations.getJSONArray("data");
			List<String> resultList = new ArrayList<>();
			int count = 0;
			while (count != jsonData.length()) {
				JSONObject jsonDataObj = jsonData.getJSONObject(count);
				JSONObject jsonDataObjMessages = jsonDataObj.getJSONObject("messages");
				JSONArray nestedDatajsonjMessages = jsonDataObjMessages.getJSONArray("data");
				count++;
				for (int i = nestedDatajsonjMessages.length() - 1; i != 0; i--) {
					JSONObject jsonMessage = nestedDatajsonjMessages.getJSONObject(i);
					String name = jsonMessage.getJSONObject("from").getString("name");
					String message = jsonMessage.getString("message");
					String conversation = name + ": " + message;
					resultList.add(conversation);
				}
			}
				return Optional.of(resultList);
		} catch (JSONException e) {
			logger.error("Can not read message from JSON ", e);
		}
		return Optional.empty();
	}
}