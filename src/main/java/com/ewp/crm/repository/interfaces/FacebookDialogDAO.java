package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.MessageDialog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacebookDialogDAO extends CommonGenericRepository<MessageDialog> {

	MessageDialog findByDialogId(String id);
}
