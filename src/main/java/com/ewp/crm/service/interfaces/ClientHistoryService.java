package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.Message;
import com.ewp.crm.models.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClientHistoryService {

	ClientHistory addHistory(ClientHistory history);

	ClientHistory createHistory(String socialRequest);

	ClientHistory createHistory(User user, Client client, ClientHistory.Type type);

	ClientHistory createHistory(User admin, User worker, Client client, ClientHistory.Type type);

	ClientHistory createHistory(User user, Client client, ClientHistory.Type type, String link);

	ClientHistory createHistory(User user, String recordLink);

	ClientHistory createHistory(User user, Client client, Message message);

	ClientHistory createHistory(User admin, Client prev, Client current, ClientHistory.Type type);

	List<ClientHistory> findByClientId(long id);

	List<ClientHistory> findAllByClientId(long id, Pageable pageable);
}
