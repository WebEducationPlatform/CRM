package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Notification;
import com.ewp.crm.models.Status;
import com.ewp.crm.models.User;

public interface SendNotificationService {
	void sendNotificationsAllUsers(Client client);
	void sendNotification(String content, Client client);
	void sendNewClientNotification(Client client, String from);
	void sendNotificationType(String info, Client client, User user, Notification.Type type);
	void sendNotificationsEditStatus(Client client, Status status);
}
