package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.EmailTemplate;
import com.ewp.crm.service.interfaces.EmailTemplateService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailTemplateDAO extends JpaRepository<EmailTemplate, Long> {
	EmailTemplate getByName(String templateName);
}
