package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Message;

import java.util.Optional;

public interface MessageService extends CommonService<Message> {
    Optional<Message> addMessage(Message.Type type, String content);
    Optional<Message> addMessage(Message.Type type, String content, String authorName);
}
