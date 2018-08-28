package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.MessageDialog;

public interface FacebookDialogService extends CommonService<MessageDialog> {

	MessageDialog addDialog(MessageDialog messageDialog);

	MessageDialog findByDialogId(String id);
}
