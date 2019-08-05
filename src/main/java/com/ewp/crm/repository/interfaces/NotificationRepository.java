package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Notification;
import com.ewp.crm.models.User;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends CommonGenericRepository<Notification> {

	void deleteByTypeAndClientAndUserToNotify(Notification.Type type, Client client, User user);

	void deleteNotificationsByClient(Client client);

	List<Notification> getByUserToNotify(User user);

	List<Notification> getByUserToNotifyAndType(User user, Notification.Type type);

	List<Notification> getByUserToNotifyAndTypeAndClient(User user, Notification.Type type, Client client);

	@Query("SELECT notify.client FROM Notification notify")
	List<Client> getClientWithNotification();

	void deleteNotificationsByUserToNotify(User user);

}
