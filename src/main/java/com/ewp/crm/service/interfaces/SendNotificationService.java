package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.User;

public interface SendNotificationService {

	void sendNotification(String content, Client client);
	void sendNotification(String info, Client client, User user);
}
