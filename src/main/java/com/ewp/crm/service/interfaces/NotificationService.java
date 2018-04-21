package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Notification;
import com.ewp.crm.models.User;
import java.util.List;
public interface NotificationService {

	void addNotification(Notification notification);

	void deleteNotificationsByClientAndUserToNotify(Client client, User user);

	List<Notification> getNotificationsByUserToNotify(User user);

}
