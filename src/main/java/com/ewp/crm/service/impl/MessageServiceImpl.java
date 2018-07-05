package com.ewp.crm.service.impl;

import com.ewp.crm.models.Message;
import com.ewp.crm.repository.interfaces.MessageRepository;
import com.ewp.crm.service.interfaces.MessageService;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl extends CommonServiceImpl<Message> implements MessageService {

	private final MessageRepository messageRepository;

	public MessageServiceImpl(MessageRepository messageRepository) {
		this.messageRepository = messageRepository;
	}

	@Override
	public Message addMessage(Message.Type type, String content) {
		return messageRepository.saveAndFlush(new Message(type, content));
	}
}
