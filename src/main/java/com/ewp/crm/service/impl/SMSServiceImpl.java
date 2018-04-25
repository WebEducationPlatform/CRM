package com.ewp.crm.service.impl;

import com.ewp.crm.configs.inteface.SMSConfig;
import com.ewp.crm.models.Client;
import com.ewp.crm.service.interfaces.SMSService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SMSServiceImpl implements SMSService {

	private static Logger logger = LoggerFactory.getLogger(SMSServiceImpl.class);

	private final SMSConfig smsConfig;
	private final RestTemplate restTemplate;

	private final String TEMPLATE_URI = "https://api.prostor-sms.ru/messages/v2";

	@Autowired
	public SMSServiceImpl(RestTemplate restTemplate, SMSConfig smsConfig) {
		this.restTemplate = restTemplate;
		this.smsConfig = smsConfig;
	}

	@Override
	public String sendSMS(Client client, String text) {
		URI uri = URI.create(TEMPLATE_URI + "/send.json");
		JSONObject jsonRequest = new JSONObject();
		JSONObject request = buildMessages(jsonRequest, Collections.singletonList(client), text);
		HttpEntity<String> entity = new HttpEntity<>(request.toString(), createHeaders());
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
		try {
			JSONObject body = new JSONObject(response.getBody());
			JSONObject message = (JSONObject) body.getJSONArray("messages").get(0);
			return message.getString("status");
		} catch (JSONException e) {
			logger.error("JSON can`t parse response");
		}
		return "Error send message";
	}

	@Override
	public String sendSMS(List<Client> clients, String text) {
		URI uri = URI.create(TEMPLATE_URI + "/send.json");
		JSONObject jsonRequest = new JSONObject();
		JSONObject request = buildMessages(jsonRequest,clients, text);
		HttpEntity<String> entity = new HttpEntity<>(request.toString(), createHeaders());
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
		try {
			JSONObject body = new JSONObject(response.getBody());
			JSONArray messages = body.getJSONArray("messages");
			StringBuilder stringBuilder = buildResponseMessage(clients,messages);
			return stringBuilder.toString();
		} catch (JSONException e) {
			logger.error("JSON can`t parse response");
		}
		return "Error to send messages";
	}


	@Override
	public String scheduledSMS(Client client, String text, String date) {
		URI uri = URI.create(TEMPLATE_URI + "/send.json");
		JSONObject jsonRequest = new JSONObject();
		try {
			jsonRequest.put("scheduleTime", date);
			JSONObject request = buildMessages(jsonRequest,Collections.singletonList(client),text);
			HttpEntity<String> entity = new HttpEntity<>(request.toString(),createHeaders());
			ResponseEntity<String> response = restTemplate.exchange(uri,HttpMethod.POST,entity, String.class);
			JSONObject body = new JSONObject(response.getBody());
			JSONObject message = (JSONObject) body.getJSONArray("messages").get(0);
			return message.getString("status");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "Error to send messages";
	}

	@Override
	public String scheduledSMS(List<Client> clients, String text, String date) {
		URI uri = URI.create(TEMPLATE_URI + "/send.json");
		JSONObject jsonRequest = new JSONObject();
		try {
			jsonRequest.put("scheduleTime", date);
			JSONObject request = buildMessages(jsonRequest,clients, text);
			HttpEntity<String> entity = new HttpEntity<>(request.toString(), createHeaders());
			ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
			JSONObject body = new JSONObject(response.getBody());
			JSONArray messages = body.getJSONArray("messages");
			StringBuilder stringBuilder = buildResponseMessage(clients,messages);
			return stringBuilder.toString();
		} catch (JSONException e) {
			logger.error("JSON can`t parse response");
		}
		return "Error to send messages";
	}

	@Override
	public String getBalance() {
		URI uri = URI.create(TEMPLATE_URI + "/balance.json");
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(createHeaders()), String.class);
		if (response.getStatusCode() == HttpStatus.OK) {
			try {
				JSONObject jsonObject = new JSONObject(response.getBody());
				JSONArray jsonArray = jsonObject.getJSONArray("balance");
				JSONObject object = (JSONObject) jsonArray.get(0);
				return object.getString("balance") + " " + object.getString("type");
			} catch (JSONException e) {
				logger.error("Can`t take balance, error authorization");
			}
		}
		return "Error authorization";
	}

	private StringBuilder buildResponseMessage(List<Client> clients, JSONArray messages) throws JSONException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Status messages:\n");
		for (int i = 0; i < messages.length(); i++) {
			stringBuilder.append(clients.get(i).getPhoneNumber());
			stringBuilder.append(" : ");
			stringBuilder.append(((JSONObject) messages.get(i)).getString("status"));
			stringBuilder.append("\n");
		}
		return stringBuilder;
	}

	private JSONObject buildMessages(JSONObject jsonRequest, List<Client> clients, String text) {
		List<JSONObject> jsonClients = new ArrayList<>();
		for (Client client : clients) {
			jsonClients.add(buildMessage(client, text));
		}
		try {
			jsonRequest.put("messages", jsonClients);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonRequest;
	}

	private JSONObject buildMessage(Client client, String text) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("phone", client.getPhoneNumber());
			jsonObject.put("sender", smsConfig.getAlphaName());
			jsonObject.put("clientId", client.getId());
			jsonObject.put("text", text);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	private HttpHeaders createHeaders() {
		String auth = smsConfig.getLogin() + ":" + smsConfig.getPassword();
		String encodeAuth = new String(Base64.encode(auth.getBytes()));
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Authorization", encodeAuth);
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		return httpHeaders;
	}
}
