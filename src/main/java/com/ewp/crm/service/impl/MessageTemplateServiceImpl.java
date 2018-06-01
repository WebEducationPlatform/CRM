package com.ewp.crm.service.impl;

import com.ewp.crm.models.MessageTemplate;
import com.ewp.crm.repository.interfaces.MessageTemplateDAO;
import com.ewp.crm.service.interfaces.MessageTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MessageTemplateServiceImpl implements MessageTemplateService {

	private final MessageTemplateDAO MessageTemplateDAO;

	@Autowired
	public MessageTemplateServiceImpl(MessageTemplateDAO MessageTemplateDAO) {
		this.MessageTemplateDAO = MessageTemplateDAO;
	}

	@Override
	public MessageTemplate get(Long id) {
		return MessageTemplateDAO.findOne(id);
	}

	@Override
	public List<MessageTemplate> getall() {
		return MessageTemplateDAO.findAll();
	}

	@Override
	public void update(MessageTemplate MessageTemplate) {
		MessageTemplateDAO.saveAndFlush(MessageTemplate);
	}

	@Override
	public void delete(Long id) {
		MessageTemplateDAO.delete(id);
	}

	@Override
	public void add(MessageTemplate MessageTemplate) {
		MessageTemplateDAO.saveAndFlush(MessageTemplate);
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
