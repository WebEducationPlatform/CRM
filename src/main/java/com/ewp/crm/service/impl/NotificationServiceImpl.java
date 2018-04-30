package com.ewp.crm.service.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Notification;
import com.ewp.crm.models.User;
import com.ewp.crm.repository.interfaces.NotificationRepository;
import com.ewp.crm.service.interfaces.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

	private final NotificationRepository notificationRepository;

	@Autowired
	public NotificationServiceImpl(NotificationRepository notificationRepository) {
		this.notificationRepository = notificationRepository;
	}

	@Override
	public void addNotification(Notification notification) {
		notificationRepository.saveAndFlush(notification);
	}

	@Override
	@Transactional
	public void deleteNotificationsByClientAndUserToNotify(Client client, User user) {
		notificationRepository.deleteNotificationsByClientAndUserToNotify(client, user);
	}

	@Transactional
	@Override
	public void deleteByTypeAndClientAndUserToNotify(Notification.Type type, Client client, User user) {
		notificationRepository.deleteByTypeAndClientAndUserToNotify(type, client, user);
	}


	@Override
	public List<Notification> getNotificationsByUserToNotify(User user) {
		return notificationRepository.getNotificationsByUserToNotify(user);
	}
}
