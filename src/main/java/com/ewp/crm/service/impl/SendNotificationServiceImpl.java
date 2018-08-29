package com.ewp.crm.service.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Notification;
import com.ewp.crm.models.User;
import com.ewp.crm.service.email.MailSendService;
import com.ewp.crm.service.interfaces.NotificationService;
import com.ewp.crm.service.interfaces.SendNotificationService;
import com.ewp.crm.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
public class SendNotificationServiceImpl implements SendNotificationService {

    private final UserService userService;

    private final MailSendService mailSendService;

    private final NotificationService notificationService;

    private static Logger logger = LoggerFactory.getLogger(SendNotificationServiceImpl.class);

    @Autowired
    public SendNotificationServiceImpl(UserService userService, MailSendService mailSendService, NotificationService notificationService) {
        this.userService = userService;
        this.mailSendService = mailSendService;
        this.notificationService = notificationService;
    }

    @Override
    public void sendNotificationsAllUsers(Client client) {
        logger.info("sending notification to all clients...");
        List<User> usersToNotify = userService.getAll();
        List<Notification> notifications = new ArrayList<>();
        for (int i = 0; i < usersToNotify.size(); i++) {
            notifications.add(new Notification(client, usersToNotify.get(i), Notification.Type.NEW_USER));
            notificationService.add(notifications.get(i));
        }
    }

    @Override
    public void sendNotification(String content, Client client) {
        logger.info("sending notification to client...");
        String regexForContent = "\\B\\@\\p{L}+";
        String regexForSplit = "(?=[A-ZА-Я])";
        Pattern pattern = Pattern.compile(regexForContent);
        Matcher matcher = pattern.matcher(content);

		User contentCreator = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String notificationMessage = String.format("Для Вас %s в комментариях под карточкой %s %s написал: \" %s \"",
				contentCreator.getFullName(), client.getLastName(), client.getName(), content);

		while (matcher.find()) {
			String[] fullName = matcher.group().split(regexForSplit);
			if (fullName.length == 3) {
				User userToNotify = userService.getUserByFirstNameAndLastName(fullName[1], fullName[2]);
				if (Optional.ofNullable(userToNotify).isPresent()) {
					if (userToNotify.isEnableMailNotifications()) {
						mailSendService.sendNotificationMessage(userToNotify, notificationMessage);
					}
					Notification notification = new Notification(client, userToNotify, Notification.Type.COMMENT);
					notificationService.add(notification);
				}
			}
		}
	}

	@Override
	public void sendNotificationType(String info, Client client, User user, Notification.Type type) {
		Notification notification = new Notification(client, user, type);
		notification.setInformation(info);
		notificationService.add(notification);
	}
}
