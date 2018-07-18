package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.MessageTemplate;

public interface MessageTemplateDAO extends CommonGenericRepository<MessageTemplate> {
	MessageTemplate getByName(String templateName);
}
