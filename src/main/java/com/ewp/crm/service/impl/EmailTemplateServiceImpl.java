package com.ewp.crm.service.impl;

import com.ewp.crm.models.EmailTemplate;
import com.ewp.crm.repository.interfaces.EmailTemplateDAO;
import com.ewp.crm.service.interfaces.EmailTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailTemplateServiceImpl implements EmailTemplateService {

	private final EmailTemplateDAO emailTemplateDAO;

	@Autowired
	public EmailTemplateServiceImpl(EmailTemplateDAO emailTemplateDAO) {
		this.emailTemplateDAO = emailTemplateDAO;
	}

	@Override
	public EmailTemplate get(Long id) {
		return emailTemplateDAO.findOne(id);
	}

	@Override
	public List<EmailTemplate> getall() {
		return emailTemplateDAO.findAll();
	}

	@Override
	public void update(EmailTemplate emailTemplate) {
		emailTemplateDAO.saveAndFlush(emailTemplate);
	}

	@Override
	public void delete(Long id) {
		emailTemplateDAO.delete(id);
	}

	@Override
	public void add(EmailTemplate emailTemplate) {
		emailTemplateDAO.saveAndFlush(emailTemplate);
	}

	@Override
	public EmailTemplate getByName(String name) {
		return emailTemplateDAO.getByName(name);
	}


}
