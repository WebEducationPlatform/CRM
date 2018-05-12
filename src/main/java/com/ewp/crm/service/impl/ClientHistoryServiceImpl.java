package com.ewp.crm.service.impl;

import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.repository.interfaces.ClientHistoryRepository;
import com.ewp.crm.service.interfaces.ClientHistoryService;
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
	public void addClientHistory(ClientHistory clientHistory) {
		determineTitleOfClientHistory(clientHistory);
		clientHistoryRepository.saveAndFlush(clientHistory);
	}

	private void determineTitleOfClientHistory(ClientHistory clientHistory) {
		ClientHistory.Type type = clientHistory.getType();
		if (type == ClientHistory.Type.SOCIAL_REQUEST) {
			clientHistory.setTitle("Поступила заявка с " + clientHistory.getSocialNetworkType().getName());
			return;
		}
		String worker = clientHistory.getUser().getFullName();
		String title;
		switch (type) {
			case SMS:
				title = worker + " отправил SMS ";
				break;
			case CALL:
				title = worker + " позвонил ";
				break;
			case STATUS:
				title = worker + " перевел клиента в статус " + clientHistory.getClient().getStatus().getName();
				break;
			case POSTPONE:
				title = worker + " скрыл клиента до " + clientHistory.getClient().getPostponeDate();
				break;
			default:
				title = "История инициализирована через пустой конструктор";
		}
		clientHistory.setTitle(title);
	}
}
