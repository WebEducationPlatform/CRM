package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.MessageTemplate;
import com.ewp.crm.service.interfaces.MessageTemplateService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageTemplateDAO extends JpaRepository<MessageTemplate, Long> {
	MessageTemplate getByName(String templateName);
}
