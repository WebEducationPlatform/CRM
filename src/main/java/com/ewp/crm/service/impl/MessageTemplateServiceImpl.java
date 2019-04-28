package com.ewp.crm.service.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.MessageTemplate;
import com.ewp.crm.repository.interfaces.MessageTemplateDAO;
import com.ewp.crm.service.interfaces.MessageTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class MessageTemplateServiceImpl extends CommonServiceImpl<MessageTemplate> implements MessageTemplateService {
	private final MessageTemplateDAO MessageTemplateDAO;

	@Autowired
	public MessageTemplateServiceImpl(MessageTemplateDAO MessageTemplateDAO) {
		this.MessageTemplateDAO = MessageTemplateDAO;
	}

	@Override
	public Optional<MessageTemplate> getByName(String name) {
		return Optional.ofNullable(MessageTemplateDAO.getByName(name));
	}

	@Override
	public String replaceName(String msg, Map<String, String> params) {
		String replaceText = msg;
		for (Map.Entry<String, String> entry : params.entrySet()) {
			replaceText = String.valueOf(new StringBuilder(replaceText.replaceAll(entry.getKey(), entry.getValue())));
		}
		return replaceText;
	}

	public String prepareText(Client client, String templateText, String body) {
		String fullName = client.getName() + " " + client.getLastName();
		Map<String, String> params = new HashMap<>();
		params.put("%fullName%", fullName);
		params.put("%bodyText%", templateText);
		params.put("%dateOfSkypeCall%", body);
		return replaceText(templateText, params);
	}

	private String replaceText(String msg, Map<String, String> params) {
		String text = msg;
		for (Map.Entry<String, String> entry : params.entrySet()) {
			text = text.replaceAll(entry.getKey(), entry.getValue());
		}
		return text;
	}
}
