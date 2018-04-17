package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.EmailTemplate;

import java.util.List;

public interface EmailTemplateService {

	EmailTemplate get(Long id);

	List<EmailTemplate> getall();

	void update(EmailTemplate emailTemplate);

	void delete(Long id);

	void add(EmailTemplate emailTemplate);

	EmailTemplate getByName(String name);
}
