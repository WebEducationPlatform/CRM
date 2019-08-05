package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Notification;
import com.ewp.crm.models.User;

import java.util.List;

public interface NotificationService extends CommonService<Notification> {
    void deleteByTypeAndClientAndUserToNotify(Notification.Type type, Client client, User user);

	void deleteNotificationsByClient(Client client);

	List<Notification> getByUserToNotify(User user);

    List<Notification> getByUserToNotifyAndType(User user, Notification.Type type);

    List<Notification> getByUserToNotifyAndTypeAndClient(User user, Notification.Type type, Client client);

    List<Client> getClientWithNotification();

    void deleteNotificationsByUserToNotify(User user);
}
