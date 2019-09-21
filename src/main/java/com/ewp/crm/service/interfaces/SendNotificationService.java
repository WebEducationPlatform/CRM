package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Notification;
import com.ewp.crm.models.User;

public interface SendNotificationService {
	void sendNotification(String content, Client client);
	void sendNotificationType(String info, Client client, User user, Notification.Type type);
}
