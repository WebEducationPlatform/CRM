package com.ewp.crm.component.util;

import com.ewp.crm.component.util.interfaces.SMSUtil;
import com.ewp.crm.configs.inteface.SMSConfig;
import com.ewp.crm.models.*;
import com.ewp.crm.service.interfaces.ClientHistoryService;
import com.ewp.crm.service.interfaces.ClientService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//TODO протестировать с async
@Component
public class SMSUtilImpl implements SMSUtil {

	private static Logger logger = LoggerFactory.getLogger(SMSUtilImpl.class);

	private final SMSConfig smsConfig;
	private final RestTemplate restTemplate;
	private final ClientService clientService;
	private final ClientHistoryService clientHistoryService;

	private final String TEMPLATE_URI = "https://api.prostor-sms.ru/messages/v2";

	@Autowired
	public SMSUtilImpl(RestTemplate restTemplate, SMSConfig smsConfig, ClientService clientService, ClientHistoryService clientHistoryService) {
		this.restTemplate = restTemplate;
		this.smsConfig = smsConfig;
		this.clientService = clientService;
		this.clientHistoryService = clientHistoryService;
	}

	@Override
	public void sendSMS(Client client, String text, User sender) {
		URI uri = URI.create(TEMPLATE_URI + "/send.json");
		JSONObject jsonRequest = new JSONObject();
		JSONObject request = buildMessages(jsonRequest, Collections.singletonList(client), text);
		HttpEntity<String> entity = new HttpEntity<>(request.toString(), createHeaders());
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
		try {
			JSONObject body = new JSONObject(response.getBody());
			JSONObject message = (JSONObject) body.getJSONArray("messages").get(0);
			SMSInfo smsInfo = new SMSInfo(message.getLong("smscId"), text, sender);
			client.addSMSInfo(smsInfo);
			Message forHistory = new Message(Message.Type.SMS, text);
			client.addHistory(clientHistoryService.createHistory(sender, client, forHistory));
			clientService.updateClient(client);
		} catch (JSONException e) {
			logger.error("Error to send message");
		}
	}

	@Override
	public void sendSMS(List<Client> clients, String text, User sender) {
		URI uri = URI.create(TEMPLATE_URI + "/send.json");
		JSONObject jsonRequest = new JSONObject();
		JSONObject request = buildMessages(jsonRequest, clients, text);
		HttpEntity<String> entity = new HttpEntity<>(request.toString(), createHeaders());
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
		try {
			JSONObject body = new JSONObject(response.getBody());
			JSONArray messages = body.getJSONArray("messages");
			for (int i = 0; i < messages.length(); i++) {
				JSONObject smsInfo = (JSONObject) messages.get(i);
				clients.get(i).addSMSInfo(new SMSInfo(smsInfo.getLong("smscId"), text, sender));
			}
		} catch (JSONException e) {
			logger.error("Error to send messages");
		}
	}

	@Override
	public void plannedSMS(Client client, String text, String date, User sender) {
		URI uri = URI.create(TEMPLATE_URI + "/send.json");
		JSONObject jsonRequest = new JSONObject();
		try {
			jsonRequest.put("scheduleTime", date);
			JSONObject request = buildMessages(jsonRequest, Collections.singletonList(client), text);
			HttpEntity<String> entity = new HttpEntity<>(request.toString(), createHeaders());
			ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
			JSONObject body = new JSONObject(response.getBody());
			JSONObject message = (JSONObject) body.getJSONArray("messages").get(0);
			SMSInfo smsInfo = new SMSInfo(message.getLong("smscId"), text, sender);
			client.addSMSInfo(smsInfo);
		} catch (JSONException e) {
			logger.error("Error to send message");
		}
	}

	@Override
	public void plannedSMS(List<Client> clients, String text, String date, User sender) {
		URI uri = URI.create(TEMPLATE_URI + "/send.json");
		JSONObject jsonRequest = new JSONObject();
		try {
			jsonRequest.put("scheduleTime", date);
			JSONObject request = buildMessages(jsonRequest, clients, text);
			HttpEntity<String> entity = new HttpEntity<>(request.toString(), createHeaders());
			ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
			JSONObject body = new JSONObject(response.getBody());
			JSONArray messages = body.getJSONArray("messages");
			for (int i = 0; i < messages.length(); i++) {
				JSONObject smsInfo = (JSONObject) messages.get(i);
				clients.get(i).addSMSInfo(new SMSInfo(smsInfo.getLong("smscId"), text, sender));
			}
		} catch (JSONException e) {
			logger.error("Error to send message");
		}
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
		return "Error";
	}

	@Override
	public String getStatusMessage(long smsId) {
		URI uri = URI.create(TEMPLATE_URI + "/status.json");
		JSONObject request = new JSONObject();
		JSONObject smsRequest = new JSONObject();
		try {
			smsRequest.put("smscId", smsId);
			JSONArray jsonArray = new JSONArray();
			jsonArray.put(smsRequest);
			request.put("messages", jsonArray);
			HttpEntity<String> entity = new HttpEntity<>(request.toString(), createHeaders());
			ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

			JSONObject body = new JSONObject(response.getBody());
			JSONObject message = (JSONObject) body.getJSONArray("messages").get(0);
			return message.getString("status");
		} catch (JSONException e) {
			logger.error("Can`t take sms status, JSON parse error");
		}

		return "Error";
	}

	private JSONObject buildMessages(JSONObject jsonRequest, List<Client> clients, String text) {
		JSONArray jsonClients = new JSONArray();
		for (Client client : clients) {
			jsonClients.put(buildMessage(client, text));
		}
		try {
			jsonRequest.put("messages", jsonClients);
		} catch (JSONException e) {
			logger.error("Can`t build JSON message {}", e.getMessage());
		}
		return jsonRequest;
	}

	private JSONObject buildMessage(Client client, String text) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("phone", client.getPhoneNumber());
			jsonObject.put("sender", smsConfig.getAlphaName());
			jsonObject.put("text", text);
		} catch (JSONException e) {
			logger.error("Can`t build JSON message {}", e.getMessage());
		}
		return jsonObject;
	}

	private HttpHeaders createHeaders() {
		String auth = smsConfig.getLogin() + ":" + smsConfig.getPassword();
		String encodeAuth = new String(Base64.encode(auth.getBytes()));
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Authorization", encodeAuth);
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.set("charset", "utf-8");
		return httpHeaders;
	}
}
