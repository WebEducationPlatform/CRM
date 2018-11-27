package com.ewp.crm.service.impl;

import com.ewp.crm.models.MessageTemplate;
import com.ewp.crm.repository.interfaces.MessageTemplateDAO;
import com.ewp.crm.service.interfaces.MessageTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MessageTemplateServiceImpl extends CommonServiceImpl<MessageTemplate> implements MessageTemplateService {
	private final MessageTemplateDAO MessageTemplateDAO;

	@Autowired
	public MessageTemplateServiceImpl(MessageTemplateDAO MessageTemplateDAO) {
		this.MessageTemplateDAO = MessageTemplateDAO;
	}

	@Override
	public MessageTemplate getByName(String name) {
		return MessageTemplateDAO.getByName(name);
	}

	@Override
	public String replaceName(String msg, Map<String, String> params) {
		String replaceText = msg;
		for (Map.Entry<String, String> entry : params.entrySet()) {
			replaceText = String.valueOf(new StringBuilder(replaceText.replaceAll(entry.getKey(), entry.getValue())));
		}
		return replaceText;
	}
}
