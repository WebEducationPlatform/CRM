package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;

public interface SendNotificationService {

	void sendNotification(String content, Client client);
}
