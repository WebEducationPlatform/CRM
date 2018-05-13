package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientHistory;

public interface ClientHistoryService {

	ClientHistory generateValidHistory(ClientHistory clientHistory, Client client);

	void addHistory(ClientHistory history);
}
