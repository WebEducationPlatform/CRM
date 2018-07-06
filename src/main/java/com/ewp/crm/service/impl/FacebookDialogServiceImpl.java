package com.ewp.crm.service.impl;

import com.ewp.crm.models.MessageDialog;
import com.ewp.crm.repository.interfaces.FacebookDialogDAO;
import com.ewp.crm.service.interfaces.FacebookDialogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacebookDialogServiceImpl implements FacebookDialogService {

	private FacebookDialogDAO facebookDialogDAO;

	@Autowired
	public FacebookDialogServiceImpl(FacebookDialogDAO facebookDialogDAO) {
		this.facebookDialogDAO = facebookDialogDAO;
	}

	@Override
	public List<MessageDialog> getAllMessageDialog() {
		return facebookDialogDAO.findAll();
	}

	@Override
	public MessageDialog getMessageDialog(Long id) {
		return facebookDialogDAO.getOne(id);
	}

	@Override
	public MessageDialog addDialog(MessageDialog messageDialog) {
		return facebookDialogDAO.saveAndFlush(messageDialog);
	}

	@Override
	public void deleteDialog(MessageDialog messageDialog) {
		facebookDialogDAO.delete(messageDialog);
	}

	@Override
	public void deleteDialog(Long id) {
		facebookDialogDAO.delete(id);
	}

	@Override
	public void updateDialog(MessageDialog messageDialog) {
		facebookDialogDAO.saveAndFlush(messageDialog);
	}
}
