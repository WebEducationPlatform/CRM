package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Notification;
import com.ewp.crm.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	void deleteNotificationsByClientAndUserToNotify(Client client, User user);

	List<Notification> getNotificationsByUserToNotify(User user);
}
