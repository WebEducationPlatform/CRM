package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.MessageDialog;

import java.util.Optional;

public interface FacebookDialogService extends CommonService<MessageDialog> {

	Optional<MessageDialog> addDialog(MessageDialog messageDialog);

	Optional<MessageDialog> getByDialogId(String id);
}
