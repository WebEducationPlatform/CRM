package com.ewp.crm.service.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Notification;
import com.ewp.crm.models.User;
import com.ewp.crm.service.email.MailNotificationService;
import com.ewp.crm.service.interfaces.NotificationService;
import com.ewp.crm.service.interfaces.SendNotificationService;
import com.ewp.crm.service.interfaces.UserService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SendNotificationServiceImpl implements SendNotificationService {

	private final UserService userService;

	private final MailNotificationService mailNotificationService;

	private final NotificationService notificationService;

	@Autowired
	public SendNotificationServiceImpl(UserService userService, MailNotificationService mailNotificationService, NotificationService notificationService) {
		this.userService = userService;
		this.mailNotificationService = mailNotificationService;
		this.notificationService = notificationService;
	}

	@Override
	public void sendNotification(String content, Client client) {
		List<User> users = userService.getAll();
		for (User user : users) {
			if (content.contains('@' + user.getFullCombinedName())) {
				User userToNotify = userService.getUserByFirstNameAndLastName(user.getFirstName(), user.getLastName());
				if (user.isEnableNotifications()) {
					mailNotificationService.sendNotificationMessage(userToNotify);
				}
				Notification notification = new Notification(client, userToNotify);
				notificationService.addNotification(notification);
			}
		}
	}
}
