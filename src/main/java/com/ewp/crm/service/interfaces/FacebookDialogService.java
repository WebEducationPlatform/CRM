package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.MessageDialog;

import java.util.List;

public interface FacebookDialogService {

	List<MessageDialog> getAllMessageDialog();

	MessageDialog getMessageDialog(Long id);

	MessageDialog addDialog(MessageDialog messageDialog);

	void deleteDialog(MessageDialog messageDialog);

	void deleteDialog(Long id);

	void updateDialog(MessageDialog messageDialog);
}
