package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Notification;
import com.ewp.crm.models.SocialProfileType;
import com.ewp.crm.models.User;

import java.util.Optional;

public interface SendNotificationService {
	void sendNotificationsAllUsers(Client client);
	void sendNotification(String content, Client client);
	void sendNewClientNotification(Client client, Optional<SocialProfileType> socialProfileType);
	void sendNotificationType(String info, Client client, User user, Notification.Type type);
}
