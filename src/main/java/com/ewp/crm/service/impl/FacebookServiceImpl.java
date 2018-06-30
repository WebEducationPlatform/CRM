package com.ewp.crm.service.impl;

import com.ewp.crm.configs.inteface.FacebookConfig;
import com.ewp.crm.exceptions.util.FBAccessTokenException;
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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FacebookServiceImpl implements FacebookService {

	private static Logger logger = LoggerFactory.getLogger(FacebookServiceImpl.class);

	private String version;
	private String pageToken;
	private String pageId;

	@Autowired
	public FacebookServiceImpl(FacebookConfig facebookConfig) {
		this.version = facebookConfig.getVersion();
		this.pageToken = facebookConfig.getPageToken();
		this.pageId = facebookConfig.getPageId();
	}

	private final String FB_API_METHOD_TEMPLATE = "https://graph.facebook.com/";

	public Optional<List<String>> getFacebookMessages() throws FBAccessTokenException {
		if (pageToken == null) {
			throw new FBAccessTokenException("Facebook access token has not got");
		}
		String uriGetMassages = FB_API_METHOD_TEMPLATE  + version + "/" + pageId + "/" +
				"?fields=about,conversations%7Bmessages%7Bfrom,message%7D%7D" +
				"&access_token=" + pageToken;
		try {
			HttpGet httpGetMessages = new HttpGet(uriGetMassages);
			HttpClient httpClient = HttpClients.custom()
					.setDefaultRequestConfig(RequestConfig.custom()
							.setCookieSpec(CookieSpecs.STANDARD).build())
					.build();
			HttpResponse response = httpClient.execute(httpGetMessages);
			String result = EntityUtils.toString(response.getEntity());
			JSONObject json = new JSONObject(result);
			JSONObject conversations = (JSONObject) json.get("conversations");
			JSONArray jsonData = conversations.getJSONArray("data");
			List<String> resultList = new ArrayList<>();
			int count = 0;
			while (jsonData.length() != 0) {

				JSONObject jsonDataObj = jsonData.getJSONObject(count);
				JSONObject jsonDataObjMessages = jsonDataObj.getJSONObject("messages");
				JSONArray nestedDatajsonjMessages = jsonDataObjMessages.getJSONArray("data");
				count++;
				for (int i = nestedDatajsonjMessages.length() - 1; i != 0; i--) {
					JSONObject jsonMessage = nestedDatajsonjMessages.getJSONObject(i);
					String name = jsonMessage.getJSONObject("from").getString("name");
					String message = jsonMessage.getString("message");
					System.out.println(name + ": " + message);
					resultList.add(jsonMessage.getString("message"));
				}
			}
				return Optional.of(resultList);
		} catch (JSONException e) {
			logger.error("Can not read message from JSON ", e);
		} catch (IOException e) {
			logger.error("Failed to connect to Facebook server ", e);
		}
		return Optional.empty();
	}
}