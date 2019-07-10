package com.ewp.crm.service.impl;

import com.ewp.crm.models.Message;
import com.ewp.crm.repository.interfaces.MessageRepository;
import com.ewp.crm.service.interfaces.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MessageServiceImpl extends CommonServiceImpl<Message> implements MessageService {

	private final MessageRepository messageRepository;

	private static Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

	public MessageServiceImpl(MessageRepository messageRepository) {
		this.messageRepository = messageRepository;
	}

	@Override
	public Optional<Message> addMessage(Message.Type type, String content) {
		logger.info("adding message...");
		return Optional.of(messageRepository.saveAndFlush(new Message(type, content)));
	}

	@Override
	public Optional<Message> addMessage(Message.Type type, String content, String authorName) {
		logger.info("adding message...");
		return Optional.of(messageRepository.saveAndFlush(new Message(type, content, authorName)));
	}
}
