package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.MessageTemplate;

import java.util.List;

public interface MessageTemplateService {

	MessageTemplate get(Long id);

	List<MessageTemplate> getall();

	void update(MessageTemplate MessageTemplate);

	void delete(Long id);

	void add(MessageTemplate MessageTemplate);

	MessageTemplate getByName(String name);
}
