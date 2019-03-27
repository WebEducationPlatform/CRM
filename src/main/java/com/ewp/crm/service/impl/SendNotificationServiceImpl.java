package com.ewp.crm.service.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Notification;
import com.ewp.crm.models.SocialProfileType;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class SendNotificationServiceImpl implements SendNotificationService {

    private final UserService userService;

    private final MailSendService mailSendService;

    private final NotificationService notificationService;

    private final SMSService smsService;

    private final VKService vkService;

    private static Logger logger = LoggerFactory.getLogger(SendNotificationServiceImpl.class);

    @Autowired
    public SendNotificationServiceImpl(UserService userService, MailSendService mailSendService, NotificationService notificationService, SMSService smsService, @Lazy VKService vkService) {
        this.userService = userService;
        this.mailSendService = mailSendService;
        this.notificationService = notificationService;
        this.smsService = smsService;
        this.vkService = vkService;
    }

    @Override
    public void sendNotificationsAllUsers(Client client) {
        logger.info("sending notification to all clients...");
        List<User> usersToNotify = userService.getAll();
        for (User user : usersToNotify) {
            if (user.isNewClientNotifyIsEnabled()) {
                notificationService.add(new Notification(client, user, Notification.Type.NEW_USER));
            }
        }
    }

    @Override
    public void sendNewClientNotification(Client client, Optional<SocialProfileType> socialProfileType) {
        String newClientUrl = "https://crm.java-mentor.com/client?id=" + client.getId();
        Optional<String> shortUrl = vkService.getShortLinkForUrl(newClientUrl);
        String notificationMessage = "Поступила новая заявка ";
        if (socialProfileType.isPresent()) {
            notificationMessage += "из " + socialProfileType.get().getName() + " ";
        }
        notificationMessage += shortUrl.orElse(newClientUrl);
        List<User> usersToNotify = userService.getAll();
        for (User userToNotify : usersToNotify) {
            if (userToNotify.isEnableMailNotifications()) {
                mailSendService.sendNotificationMessage(userToNotify, notificationMessage);
            }
            if (userToNotify.isEnableSmsNotifications()) {
                smsService.sendSimpleSMS(userToNotify.getId(), notificationMessage.replace("https://", ""));
            }
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
                Optional<User> userToNotify = userService.getUserByFirstNameAndLastName(fullName[1], fullName[2]);
                if (userToNotify.isPresent()) {
                    if (userToNotify.get().isEnableMailNotifications()) {
                        mailSendService.sendNotificationMessage(userToNotify.get(), notificationMessage);
                    }
                    Notification notification = new Notification(client, userToNotify.get(), Notification.Type.COMMENT);
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
