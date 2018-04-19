package com.ewp.crm.service.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Notification;
import com.ewp.crm.models.User;
import com.ewp.crm.service.email.MailSendService;
import com.ewp.crm.service.interfaces.NotificationService;
import com.ewp.crm.service.interfaces.SendNotificationService;
import com.ewp.crm.service.interfaces.UserService;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SendNotificationServiceImpl implements SendNotificationService {

	private final UserService userService;

	private final MailSendService mailSendService;

	private final NotificationService notificationService;

	@Autowired
	public SendNotificationServiceImpl(UserService userService, MailSendService mailSendService, NotificationService notificationService) {
		this.userService = userService;
		this.mailSendService = mailSendService;
		this.notificationService = notificationService;
	}

	@Override
	public void sendNotification(String content, Client client) {
		String regexForContent = "\\B\\@\\w+";
		String regexForSplit = "(?=[A-Z])";
		Pattern pattern = Pattern.compile(regexForContent);
		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			String[] result = matcher.group().split(regexForSplit);
			User userToNotify = userService.getUserByFirstNameAndLastName(result[1], result[2]);
			if (userToNotify.isEnableNotifications()) {
				mailSendService.sendNotificationMessage(userToNotify);
			}
			Notification notification = new Notification(client, userToNotify);
			notificationService.addNotification(notification);
		}
	}
}
