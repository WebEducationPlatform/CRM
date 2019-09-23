package com.ewp.crm.service.impl;

import com.ewp.crm.configs.VKConfigImpl;
import com.ewp.crm.models.*;
import com.ewp.crm.service.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class SendNotificationServiceImpl implements SendNotificationService {

    private final String serverUrl;

    private final ProjectProperties projectProperties;

    private final MessageTemplateService messageTemplateService;

    private final UserService userService;

    private final MailSendService mailSendService;

    private final NotificationService notificationService;

    private final SMSService smsService;

    private final VKService vkService;

    private final UserStatusService userStatusService;

    private final StatusService statusService;

    private static Logger logger = LoggerFactory.getLogger(SendNotificationServiceImpl.class);

    private Environment env;


    @Autowired
    public SendNotificationServiceImpl(MessageTemplateService messageTemplateService,
                                       ProjectPropertiesService projectPropertiesService,
                                       Environment env, UserService userService,
                                       MailSendService mailSendService,
                                       NotificationService notificationService,
                                       SMSService smsService, @Lazy VKService vkService,
                                       VKConfigImpl vkConfig, UserStatusService userStatusService,
                                       StatusService statusService) {
        this.userService = userService;
        this.mailSendService = mailSendService;
        this.notificationService = notificationService;
        this.smsService = smsService;
        this.vkService = vkService;
        this.messageTemplateService = messageTemplateService;
        this.projectProperties = projectPropertiesService.getOrCreate();
        this.serverUrl = env.getProperty("server.url");
        this.env = env;
        this.userStatusService = userStatusService;
        this.statusService = statusService;
    }


    @Override
    public void sendNotificationsEditStatus(Client client, Status status){
        logger.info("sending notification to edit status clients...");
        List<User> usersToNotify = userService.getAll();
        for (User user : usersToNotify) {
            if (userStatusService.getUserStatus(user.getId(), status.getId()).getSendNotifications()) {
                notificationService.add(new Notification(status.toString(), client, user, Notification.Type.EDIT_STATUS));
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
        String notificationMessage = String.format(env.getProperty("messaging.notification.set-comment"),
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
