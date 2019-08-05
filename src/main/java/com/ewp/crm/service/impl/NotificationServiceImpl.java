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
public class NotificationServiceImpl extends CommonServiceImpl<Notification> implements NotificationService {
	private final NotificationRepository notificationRepository;

	@Autowired
	public NotificationServiceImpl(NotificationRepository notificationRepository) {
		this.notificationRepository = notificationRepository;
	}

	@Override
	@Transactional
	public void deleteByTypeAndClientAndUserToNotify(Notification.Type type, Client client, User user) {
		notificationRepository.deleteByTypeAndClientAndUserToNotify(type, client, user);
	}

	@Override
	@Transactional
	public void deleteNotificationsByClient(Client client) {
		notificationRepository.deleteNotificationsByClient(client);
	}

	@Override
	public List<Notification> getByUserToNotify(User user) {
		return notificationRepository.getByUserToNotify(user);
	}

	@Override
	public List<Notification> getByUserToNotifyAndType(User user, Notification.Type type) {
		return notificationRepository.getByUserToNotifyAndType(user, type);
	}

	@Override
	public List<Notification> getByUserToNotifyAndTypeAndClient(User user, Notification.Type type, Client client) {
		return notificationRepository.getByUserToNotifyAndTypeAndClient(user, type, client);
	}

	@Override
	public List<Client> getClientWithNotification() {
		return notificationRepository.getClientWithNotification();
	}

	@Override
	public void deleteNotificationsByUserToNotify(User user) {
		notificationRepository.deleteNotificationsByUserToNotify(user);
	}

}
