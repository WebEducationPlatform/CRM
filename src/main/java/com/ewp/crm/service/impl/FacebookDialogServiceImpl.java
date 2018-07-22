package com.ewp.crm.service.impl;

import com.ewp.crm.models.MessageDialog;
import com.ewp.crm.repository.interfaces.FacebookDialogDAO;
import com.ewp.crm.service.interfaces.FacebookDialogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class FacebookDialogServiceImpl extends CommonServiceImpl<MessageDialog> implements FacebookDialogService {

	private FacebookDialogDAO facebookDialogDAO;

	@Autowired
	public FacebookDialogServiceImpl(FacebookDialogDAO facebookDialogDAO) {
		this.facebookDialogDAO = facebookDialogDAO;
	}

	@Override
	public void addDialog(MessageDialog messageDialog) {
		facebookDialogDAO.saveAndFlush(messageDialog);
	}

	@Override
	public MessageDialog findByDialogId(String id) {
		return facebookDialogDAO.findByDialogId(id);
	}
}
