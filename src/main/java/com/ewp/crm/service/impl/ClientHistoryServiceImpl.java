package com.ewp.crm.service.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.Message;
import com.ewp.crm.models.User;
import com.ewp.crm.repository.interfaces.ClientHistoryRepository;
import com.ewp.crm.service.interfaces.ClientHistoryService;
import com.ewp.crm.service.interfaces.MessageService;
import org.javers.core.Javers;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientHistoryServiceImpl implements ClientHistoryService {

	private final ClientHistoryRepository clientHistoryRepository;

	private static Logger logger = LoggerFactory.getLogger(ClientHistoryServiceImpl.class);
	private final MessageService messageService;
	private final Javers javers;

	@Autowired
	public ClientHistoryServiceImpl(ClientHistoryRepository clientHistoryRepository, MessageService messageService, Javers javers) {
		this.clientHistoryRepository = clientHistoryRepository;
		this.messageService = messageService;
		this.javers = javers;
	}

	@Override
	public ClientHistory addHistory(ClientHistory history) {
		return clientHistoryRepository.saveAndFlush(history);
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

	/*
		worker send message by email/vk/facebook/sms [link]
	 */
	@Override
	public ClientHistory createHistory(User user, Client client, Message message) {
		ClientHistory clientHistory = new ClientHistory(ClientHistory.Type.SEND_MESSAGE);
		clientHistory.setMessage(message);
		clientHistory.setLink("/client/message/info/" + message.getId());
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
		clientHistory.setTitle(admin.getFullName() + " " + type.getInfo());
		// if user updated client, which have no changes.
		if (prev.equals(current)) {
			return clientHistory;
		}
		String buildChanges = buildChanges(prev, current);
		Message message = messageService.addMessage(Message.Type.DATA, buildChanges);
		clientHistory.setMessage(message);
		clientHistory.setLink("/client/message/info/" + message.getId());
		return clientHistory;
	}


	//Use Javers Library
	private String buildChanges(Client prev, Client current) {
		StringBuilder stringBuilder = new StringBuilder();
		Diff diff = javers.compare(prev, current);
		for (Change change : diff.getChanges()) {
			stringBuilder.append(change);
			stringBuilder.append("<br>");
		}
		return stringBuilder.toString();
	}
}
