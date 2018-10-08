package com.ewp.crm.service.impl;

import com.ewp.crm.configs.inteface.SMSConfig;
import com.ewp.crm.models.*;
import com.ewp.crm.service.interfaces.*;
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
import java.util.*;

@Component
public class SMSServiceImpl implements SMSService {

	private static Logger logger = LoggerFactory.getLogger(SMSServiceImpl.class);
	private final SMSConfig smsConfig;
	private final RestTemplate restTemplate;
	private final ClientService clientService;
	private final ClientHistoryService clientHistoryService;
	private final SMSInfoService smsInfoService;
	private final MessageTemplateService messageTemplateService;
	private final String TEMPLATE_URI = "https://api.prostor-sms.ru/messages/v2";

	@Autowired
	public SMSServiceImpl(RestTemplate restTemplate, SMSConfig smsConfig, ClientService clientService, ClientHistoryService clientHistoryService, SMSInfoService smsInfoService, MessageTemplateService messageTemplateService) {
		this.restTemplate = restTemplate;
		this.smsConfig = smsConfig;
		this.clientService = clientService;
		this.clientHistoryService = clientHistoryService;
		this.smsInfoService = smsInfoService;
		this.messageTemplateService = messageTemplateService;
	}

	@Override
	public void sendSMS(Long clientId, String smsTemplateText, String body, User principal) throws JSONException {
		logger.info("{} sending sms message to client...", SMSServiceImpl.class.getName());
		Client client = clientService.getClientByID(clientId);
		String fullName = client.getName() + " " + client.getLastName();
		Map<String, String> params = new HashMap<>();
		//TODO в конфиг
		params.put("%fullName%", fullName);
		params.put("%bodyText%", body);
		params.put("%dateOfSkypeCall%", body);
		String smsText = messageTemplateService.replaceName(smsTemplateText, params);
		URI uri = URI.create(TEMPLATE_URI + "/send.json");
		JSONObject jsonRequest = new JSONObject();
		JSONObject request = buildMessages(jsonRequest, Collections.singletonList(client), smsText);
		HttpEntity<String> entity = new HttpEntity<>(request.toString(), createHeaders());
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
		JSONObject jsonBody = new JSONObject(response.getBody());
		JSONObject message = (JSONObject) jsonBody.getJSONArray("messages").get(0);
		SMSInfo smsInfo = new SMSInfo(message.getLong("smscId"), smsText, principal);
		client.addSMSInfo(smsInfoService.addSMSInfo(smsInfo));
		ClientHistory clientHistory = clientHistoryService.createHistory(principal, client, new Message(Message.Type.SMS, smsInfo.getMessage()));
		clientHistory.setLink("/client/sms/info/" + smsInfo.getId());
		client.addHistory(clientHistory);
		clientService.updateClient(client);
		logger.info("{} sms sent successfully...", SMSServiceImpl.class.getName());
	}

	@Override
	public void sendSimpleSMS(Long clientId, String smsTemplateText) {
		logger.info("{} sending sms message to client...", SMSServiceImpl.class.getName());
		Client client = clientService.getClientByID(clientId);
		String fullName = client.getName() + " " + client.getLastName();
		String smsText = smsTemplateText.replaceAll("%fullName%", fullName);
		URI uri = URI.create(TEMPLATE_URI + "/send.json");
		JSONObject jsonRequest = new JSONObject();
		JSONObject request = buildMessages(jsonRequest, Collections.singletonList(client), smsText);
		HttpEntity<String> entity = new HttpEntity<>(request.toString(), createHeaders());
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
		try {
			JSONObject jsonBody = new JSONObject(response.getBody());
			JSONObject message = null;
			message = (JSONObject) jsonBody.getJSONArray("messages").get(0);
			SMSInfo smsInfo = new SMSInfo(message.getLong("smscId"), smsText);
			client.addSMSInfo(smsInfoService.addSMSInfo(smsInfo));
			clientService.updateClient(client);
		} catch (JSONException e) {
			logger.info("JSON object error, while sending SMS to client {}", client, e);
		}
		logger.info("SMS sent successfully to the client {}", client);
	}

	@Override
	public void sendSMS(List<Client> clients, String text, User sender) {
		logger.info("{} sending sms message to clients...", SMSServiceImpl.class.getName());
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
			logger.info("{} sms sent successfully...", SMSServiceImpl.class.getName());
		} catch (JSONException e) {
			logger.error("Error to send messages ", e);
		}
		logger.info("{} sms sent successfully...", SMSServiceImpl.class.getName());
	}

	@Override
	public void plannedSMS(Client client, String text, String date, User sender) {
		logger.info("{} planning sms message to clients...", SMSServiceImpl.class.getName());
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
			logger.info("{} sms planned successfully...", SMSServiceImpl.class.getName());
		} catch (JSONException e) {
			logger.error("Error to plan sms message ", e);
		}
	}

	@Override
	public void plannedSMS(List<Client> clients, String text, String date, User sender) {
		logger.info("{} planning sms message to clients...", SMSServiceImpl.class.getName());
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
			logger.info("{} sms planned successfully...", SMSServiceImpl.class.getName());
		} catch (JSONException e) {
			logger.error("Error to plan sms message ", e);
		}
	}

	@Override
	public String getBalance() {
		logger.info("{} getting balance...", SMSServiceImpl.class.getName());
		URI uri = URI.create(TEMPLATE_URI + "/balance.json");
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(createHeaders()), String.class);
		if (response.getStatusCode() == HttpStatus.OK) {
			try {
				JSONObject jsonObject = new JSONObject(response.getBody());
				JSONArray jsonArray = jsonObject.getJSONArray("balance");
				JSONObject object = (JSONObject) jsonArray.get(0);
				return object.getString("balance") + " " + object.getString("type");
			} catch (JSONException e) {
				logger.error("Can`t take balance, error authorization ", e);
			}
		}
		return "Error";
	}

	@Override
	public String getStatusMessage(long smsId) {
		logger.info("{} getting of a status message...", SMSServiceImpl.class.getName());
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
			logger.error("Can`t take sms status, JSON parse error ", e);
		}

		return "Error";
	}

	private JSONObject buildMessages(JSONObject jsonRequest, List<Client> clients, String text) {
		logger.info("{} building messages...", SMSServiceImpl.class.getName());
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
		logger.info("{} building messages...", SMSServiceImpl.class.getName());
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
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		return httpHeaders;
	}

	@Override
	public void sendSMS(Set<ClientData> phoneNumbers, String text) {
		URI uri = URI.create(TEMPLATE_URI + "/send.json");
		JSONObject jsonRequest = new JSONObject();
		JSONObject request = buildMessages(jsonRequest, phoneNumbers, text);
		HttpEntity<String> entity = new HttpEntity<>(request.toString(), createHeaders());
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
		try {
			JSONObject body = new JSONObject(response.getBody());
			JSONArray messages = body.getJSONArray("messages");
		} catch (JSONException e) {
			logger.error("Error to send messages ", e);
		}
	}

	private JSONObject buildMessages(JSONObject jsonRequest, Set<ClientData> phoneNumber, String text) {
		JSONArray jsonClients = new JSONArray();
		for (ClientData phone : phoneNumber) {
			jsonClients.put(buildMessage(phone.getInfo(), text));
		}
		try {
			jsonRequest.put("messages", jsonClients);
		} catch (JSONException e) {
			logger.error("Can`t build JSON message {}", e.getMessage());
		}
		return jsonRequest;
	}

	private JSONObject buildMessage(String phone, String text) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("phone", phone);
			jsonObject.put("sender", smsConfig.getAlphaName());
			jsonObject.put("text", text);
		} catch (JSONException e) {
			logger.error("Can`t build JSON message {}", e.getMessage());
		}
		return jsonObject;
	}
}
