package com.ewp.crm.service.impl;

import com.ewp.crm.models.FacebookMessage;
import com.ewp.crm.repository.interfaces.FacebookMessageDAO;
import com.ewp.crm.service.interfaces.FacebookMessageServie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class FacebookMessageServiceImpl extends CommonServiceImpl<FacebookMessage> implements FacebookMessageServie {

	private final FacebookMessageDAO facebookMessageDao;

	@Autowired
	public FacebookMessageServiceImpl(FacebookMessageDAO facebookMessageDao) {
		this.facebookMessageDao = facebookMessageDao;
	}

	@Override
	public Date findMaxDate() {
		return facebookMessageDao.findMaxDate();
	}

	@Override
	public FacebookMessage addFacebookMessage(FacebookMessage facebookMessage) {
		return facebookMessageDao.saveAndFlush(facebookMessage);
	}

	@Override
	public void addBatchMessages(List<FacebookMessage> messages) {
		for (FacebookMessage msg : messages) {
			facebookMessageDao.saveAndFlush(msg);
		}
	}
}