package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Message;

public interface MessageService {

	Message addMessage(Message.Type type, String content);

	Message getById(long id);
}
