package com.ewp.crm.service.impl;

import com.ewp.crm.models.FacebookMessage;
import com.ewp.crm.repository.interfaces.FacebookMessageDAO;
import com.ewp.crm.service.interfaces.FacebookMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FacebookMessageServiceImpl extends CommonServiceImpl<FacebookMessage> implements FacebookMessageService {

	private static Logger logger = LoggerFactory.getLogger(FacebookMessageServiceImpl.class);

	private final FacebookMessageDAO facebookMessageDao;

	@Autowired
	public FacebookMessageServiceImpl(FacebookMessageDAO facebookMessageDao) {
		this.facebookMessageDao = facebookMessageDao;
	}

	@Override
	public LocalDateTime findMaxDate() {
		return facebookMessageDao.findMaxDate();
	}

	@Override
	public FacebookMessage addFacebookMessage(FacebookMessage facebookMessage) {
		logger.info("adding of facebook message...");
		return facebookMessageDao.saveAndFlush(facebookMessage);
	}

	@Override
	public void addBatchMessages(List<FacebookMessage> messages) {
		logger.info("adding of facebook messages...");
		for (FacebookMessage msg : messages) {
			facebookMessageDao.saveAndFlush(msg);
		}
	}
}