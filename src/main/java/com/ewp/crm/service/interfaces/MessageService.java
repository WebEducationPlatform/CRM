package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Message;

public interface MessageService extends CommonService<Message> {
    Message addMessage(Message.Type type, String content);
}
