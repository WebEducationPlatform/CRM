package com.ewp.crm.service.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.repository.interfaces.ClientHistoryRepository;
import com.ewp.crm.service.interfaces.ClientHistoryService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientHistoryServiceImpl implements ClientHistoryService {

	private final ClientHistoryRepository clientHistoryRepository;

	@Autowired
	public ClientHistoryServiceImpl(ClientHistoryRepository clientHistoryRepository) {
		this.clientHistoryRepository = clientHistoryRepository;
	}

	@Override
	public void addHistory(ClientHistory history) {
		clientHistoryRepository.saveAndFlush(history);
	}

	@Override
	public ClientHistory generateValidHistory(ClientHistory clientHistory, Client client) {
		ClientHistory.Type type = clientHistory.getType();
		if (type == ClientHistory.Type.SYSTEM) {
			clientHistory.setTitle("Клиент был добавлен при инициализации CRM");
			return clientHistory;
		}
		if (type == ClientHistory.Type.SOCIAL_REQUEST) {
			clientHistory.setTitle("Поступила заявка с " + clientHistory.getSocialNetworkType().getName());
			return clientHistory;
		}
		String worker = clientHistory.getUser().getFullName();
		if (type == ClientHistory.Type.SEND_MESSAGE) {
			clientHistory.setTitle(worker + " " + type.getTitle() + " " + clientHistory.getMessage().getType().getInfo());
			return clientHistory;
		}
		String title;
		DateTime dateTime = null;
		if (type == ClientHistory.Type.POSTPONE) {
			dateTime = new DateTime(client.getPostponeDate());
		}
		switch (type) {
			case ADD_CLIENT:
			case UPDATE_CLIENT:
			case SMS:
			case CALL:
			case NOTIFICATION_POSTPONE:
			case DESCRIPTION:
				title = worker + " " + type.getTitle();
				break;
			case STATUS:
				title = worker + " " + type.getTitle() + " " + client.getStatus().getName();
				break;
			case POSTPONE:
				title = worker + " " + type.getTitle() + " " + dateTime.toString("dd MMM yyyy 'года' HH:mm");
				break;
			default:
				title = "неизвестная ошибка";
		}
		clientHistory.setTitle(title);
		return clientHistory;
	}
}
