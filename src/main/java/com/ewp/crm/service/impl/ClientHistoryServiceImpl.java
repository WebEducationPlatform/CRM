package com.ewp.crm.service.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.Message;
import com.ewp.crm.models.User;
import com.ewp.crm.repository.interfaces.ClientHistoryRepository;
import com.ewp.crm.service.interfaces.ClientHistoryService;
import com.ewp.crm.service.interfaces.MessageService;
import org.apache.commons.lang3.builder.DiffResult;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientHistoryServiceImpl implements ClientHistoryService {

	private final ClientHistoryRepository clientHistoryRepository;

	private static Logger logger = LoggerFactory.getLogger(ClientHistoryServiceImpl.class);
	private final MessageService messageService;

	@Autowired
	public ClientHistoryServiceImpl(ClientHistoryRepository clientHistoryRepository, MessageService messageService) {
		this.clientHistoryRepository = clientHistoryRepository;
		this.messageService = messageService;
	}

	@Override
	public ClientHistory addHistory(ClientHistory history) {
		return clientHistoryRepository.saveAndFlush(history);
	}

	@Override
	public List<ClientHistory> findByClientId(long id) {
		return clientHistoryRepository.findByClientId(id);
	}

	@Override
	public List<ClientHistory> findAllByClientId(long id, Pageable pageable) {
		return clientHistoryRepository.findAllByClientId(id, pageable);
	}

	@Override
	public ClientHistory createHistory(String socialRequest) {
		ClientHistory clientHistory = new ClientHistory(ClientHistory.Type.SOCIAL_REQUEST);
		clientHistory.setTitle(ClientHistory.Type.SOCIAL_REQUEST.getInfo() + " " + socialRequest);
		return clientHistory;
	}

	/*
		worker change status on [status]
		worker add description to client "[description]"
		worker postpone client until [date]
		worker check notification
		worker assigned
		worker unassigned
	*/
	@Override
	public ClientHistory createHistory(User user, Client client, ClientHistory.Type type) {
		ClientHistory clientHistory = new ClientHistory(type);
		String action = user.getFullName() + " " + type.getInfo();
		StringBuilder title = new StringBuilder(action);
		switch (type) {
			case DESCRIPTION:
				title.append(" ").append("\"").append(client.getClientDescriptionComment()).append("\"");
				break;
			case POSTPONE:
				title.append(" ");
				title.append("(");
				title.append(new DateTime(client.getPostponeDate()).toString("dd MMM 'в' HH:mm yyyy'г'"));
				title.append(")");
				break;
			case STATUS:
				title.append(" ").append(client.getStatus());
				break;
			case ASSIGN:
			case UNASSIGN:
				title.append("ся");
				break;
		}
		clientHistory.setTitle(title.toString());
		return clientHistory;
	}

	/*
		admin assigned worker to client
		admin unassigned worker on client
	 */
	@Override
	public ClientHistory createHistory(User admin, User worker, Client client, ClientHistory.Type type) {
		ClientHistory clientHistory = new ClientHistory(type);
		clientHistory.setTitle(admin.getFullName() + " " + type.getInfo() + " " + worker.getFullName());
		return clientHistory;
	}

	// worker call to client [link]
	@Override
	public ClientHistory createHistory(User user, Client client, ClientHistory.Type type, String link) {
		ClientHistory clientHistory = new ClientHistory(type);
		clientHistory.setLink(link);
		clientHistory.setTitle(user.getFullName() + " " + type.getInfo());
		return clientHistory;
	}

	@Override
	public ClientHistory createHistory(User user , String recordLink) {
		ClientHistory clientHistory = new ClientHistory(ClientHistory.Type.CALL);
		clientHistory.setRecordLink(recordLink);
		clientHistory.setTitle(user.getFullName() + " " + ClientHistory.Type.CALL.getInfo());
		return clientHistory;
	}


	/*
		worker send message by email/vk/facebook/sms [link]
	 */
	@Override
	public ClientHistory createHistory(User user, Client client, Message message) {
		ClientHistory clientHistory = new ClientHistory(ClientHistory.Type.SEND_MESSAGE);
		clientHistory.setMessage(message);
		clientHistory.setLink(message.getId().toString());
		clientHistory.setTitle(user.getFullName() + " " + clientHistory.getType().getInfo() + " " + message.getType().getInfo());
		return clientHistory;
	}

	/*
		Worker add new client
		Worker change client data [link]
	 */
	@Override
	public ClientHistory createHistory(User admin, Client prev, Client current, ClientHistory.Type type) {
		ClientHistory clientHistory = new ClientHistory(type);

        // if user updated client, which has no changes.
        if (current.equals(prev)) {
            logger.info("Can't find changes");
            return clientHistory;
        }

        clientHistory.setTitle(admin.getFullName() + " " + type.getInfo());

		DiffResult diffs = prev.diff(current);

        StringBuilder content = new StringBuilder();

		diffs.getDiffs().stream().map(
				d -> d.getFieldName() + ": " + d.getLeft() + " -> " + d.getRight())
				.forEach(str -> content.append(str).append("\n"));

		Message message = messageService.addMessage(Message.Type.DATA, content.toString());

		clientHistory.setMessage(message);

		clientHistory.setLink(message.getId().toString());
		return clientHistory;
	}
}
