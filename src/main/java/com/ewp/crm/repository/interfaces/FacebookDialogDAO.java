package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.MessageDialog;

public interface FacebookDialogDAO extends CommonGenericRepository<MessageDialog> {

	MessageDialog getByDialogId(String id);
}
