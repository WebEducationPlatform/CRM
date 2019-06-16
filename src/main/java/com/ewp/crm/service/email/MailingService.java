package com.ewp.crm.service.email;


import com.ewp.crm.configs.inteface.VKConfig;
import com.ewp.crm.exceptions.parse.ParseMailingDataException;
import com.ewp.crm.models.ClientData;
import com.ewp.crm.models.MailingMessage;
import com.ewp.crm.models.User;
import com.ewp.crm.repository.interfaces.MailingMessageRepository;
import com.ewp.crm.service.interfaces.SMSService;
import com.ewp.crm.service.interfaces.SlackService;
import com.ewp.crm.service.interfaces.VKService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
//@EnableAsync
public class MailingService {
    private static Logger logger = LoggerFactory.getLogger(MailSendServiceImpl.class);
    private static LocalDateTime vkMessageNextSendTime = LocalDateTime.now();
    private static final String EMAIL_PATTERN = "\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}\\b";
    private static final String SMS_PATTERN = "\\d{11}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}";
    private static final String SLACK_PATTERN = ".+";

    private final JavaMailSender javaMailSender;
    private final SMSService smsService;
    private final VKService vkService;
    private final SlackService slackService;
    private final MailingMessageRepository mailingMessageRepository;
    private final TemplateEngine htmlTemplateEngine;
    private final VKConfig vkConfig;

    @Autowired
    public MailingService(SMSService smsService, VKService vkService, JavaMailSender javaMailSender,
                          MailingMessageRepository mailingMessageRepository, TemplateEngine htmlTemplateEngine,
                          SlackService slackService, VKConfig vkConfig) {
        this.smsService = smsService;
        this.vkService = vkService;
        this.javaMailSender = javaMailSender;
        this.mailingMessageRepository = mailingMessageRepository;
        this.htmlTemplateEngine = htmlTemplateEngine;
        this.slackService = slackService;
        this.vkConfig = vkConfig;
    }

    public MailingMessage addMailingMessage(MailingMessage message) {
        return mailingMessageRepository.saveAndFlush(message);
    }

    public void sendMessages() {
        LocalDateTime currentTime = LocalDateTime.now();
        List<MailingMessage> messages = mailingMessageRepository.getAllByReadedMessageIsFalse();
        messages.forEach(message -> {
            if (message.getDate().compareTo(currentTime) < 0) {
                // VK messages from user's page are sending with limit
                if ("vk".equals(message.getType()) && !"managerPage".equals(message.getVkType())) {
                    if (vkMessageNextSendTime.isBefore(currentTime)) {
                        // Next message will be send after 72 minutes (limit is 20 messages per day)
                        // plus some random time to avoid anti-spam blocking
                        vkMessageNextSendTime = vkMessageNextSendTime.plusMinutes(72);
                        vkMessageNextSendTime = vkMessageNextSendTime.plusSeconds(new Random().nextInt(300));
                        sendMessage(message);
                    }
                } else {
                    sendMessage(message);
                }
            }
        });
    }

    public boolean sendMessage(MailingMessage message) {
        boolean result = true;
        switch (message.getType()) {
            case "email":
                result = sendingMailingsEmails(message);
                break;
            case "sms":
                sendingMailingSMS(message);
                break;
            case "vk":
                if (message.getVkType().equals("managerPage")) {
                    sendingMailingVkWithManagerAccount(message);
                } else {
                    sendingMailingVk(message);
                }
                break;
            case "slack":
                sendingMailingSlack(message);
                break;
        }
        return result;
    }

    private boolean sendingMailingsEmails(MailingMessage message) {
        boolean result = false;
        try {
            final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            final MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            for (ClientData email : message.getClientsData()) {
                mimeMessageHelper.setFrom("Java-Mentor.ru");
                mimeMessageHelper.setTo(email.getInfo());
                mimeMessageHelper.setSubject("Ваш личный Java наставник");
                final Context ctx = new Context();
                String templateText = message.getText().replaceAll("\n", "");
                ctx.setVariable("templateText", templateText);
                StringBuilder htmlContent = new StringBuilder(htmlTemplateEngine.process("emailStringTemplate", ctx));
                mimeMessageHelper.setText(htmlContent.toString(), true);
                Pattern pattern = Pattern.compile("(?<=cid:)\\S*(?=\\|)");
                Matcher matcher = pattern.matcher(message.getText());
                while (matcher.find()) {
                    String path = (matcher.group()).replaceAll("/", "\\" + File.separator);
                    File file = new File(path);
                    if (file.exists()) {
                        InputStreamSource inputStreamSource = new FileSystemResource(file);
                        mimeMessageHelper.addInline(matcher.group(), inputStreamSource, "image/jpeg");
                    } else {
                        logger.error("Can not send message! Attachment file {} not found. Reimport file.", file.getCanonicalPath());
                        return false;
                    }
                }
                javaMailSender.send(mimeMessage);
            }
            message.setReadedMessage(true);
            mailingMessageRepository.save(message);
            result = true;
        } catch (MessagingException e) {
            logger.info("Message no sent.", e);
        } catch (NullPointerException e) {
            logger.info("No recipients found, clientData is empty.", e);
        } catch (IOException e) {
            logger.info("Can not read template file.", e);
        }
        return result;
    }

    private void sendingMailingSMS(MailingMessage message) {
        smsService.sendSMS(message.getClientsData(), message.getText());
        message.setReadedMessage(true);
        mailingMessageRepository.save(message);
    }

    private void sendingMailingSlack(MailingMessage message) {
        List<String> notSendList = new ArrayList<>();
        message.getClientsData().forEach(c -> {
            try {
                boolean sendResult = slackService.trySendMessageToSlackUser(c.getInfo(), message.getText());
                if (!sendResult) {
                    notSendList.add(c.getInfo());
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                logger.error("a lot of requests", e);
            }
        });
        message.setReadedMessage(true);
        message.setNotSendId(notSendList);
        mailingMessageRepository.save(message);
    }

    private void sendingMailingVk(MailingMessage message) {
        List<String> notSendList = new ArrayList<>();
        for (ClientData idVk : message.getClientsData()) {
            try {
                Thread.sleep(1000);
                String value = vkService.sendMessageById(Long.parseLong(idVk.getInfo()), message.getText(), vkConfig.getCommunityToken());
                if (!value.equalsIgnoreCase("Message sent")) {
                    notSendList.add(value);
                }
                message.setReadedMessage(true);
            } catch (ClassCastException e) {
                logger.info("bad vk id, " + idVk + ", ", e);
            } catch (InterruptedException e) {
                logger.error("a lot of requests", e);
            }
        }
        message.setNotSendId(notSendList);
        mailingMessageRepository.save(message);
    }

    private void sendingMailingVkWithManagerAccount(MailingMessage message) {
        List<String> notSendList = new ArrayList<>();
        for (ClientData idVk : message.getClientsData()) {
            try {
                Thread.sleep(1000);
                String value = vkService.sendMessageById(Long.parseLong(idVk.getInfo()), message.getText());
                if (!value.equalsIgnoreCase("Message sent")) {
                    notSendList.add(value);
                    message.setNotSendId(notSendList);
                }
                message.setReadedMessage(true);
            } catch (ClassCastException e) {
                logger.info("bad vk id, " + idVk + ", ", e);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mailingMessageRepository.save(message);
    }

    private void addVkMailingToSendQueue(String recipients, String text, LocalDateTime destinationDate, String vkType, User user) {
        // Preparing messages:
        // 0. Get recipient's id from links in 'recipients' String
        // 1. Put every recipient's id to clientsInfo HashSet to avoid duplicates
        // 2. Create message for every recipient from 'recipients' String
        Set<ClientData> clientsInfo = new HashSet<>();
        List<MailingMessage> vkMessages = new ArrayList<>();
        Arrays.asList(recipients.split("\n")).forEach(recipient -> vkService.getIdFromLink(recipient.trim()).ifPresent(id -> clientsInfo.add(new ClientData(id))));
        clientsInfo.forEach(c -> vkMessages.add(new MailingMessage("vk", text, new HashSet<>(Collections.singletonList(c)), destinationDate, vkType, user.getId())));
        // Adding all messages to queue
        vkMessages.forEach(this::addMailingMessage);
    }

    private void addMailingToSendQueue(String messageType, String recipients, String text, String pattern, LocalDateTime destinationDate, User user) {
        Set<ClientData> clientsInfo = new HashSet<>();
        // Only get recipients who matches address pattern and add them to HashMap to avoid duplicates
        Matcher recipientMatcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(recipients);
        while (recipientMatcher.find()) {
            clientsInfo.add(new ClientData(recipientMatcher.group()));
        }
        // Adding message to queue
        MailingMessage message = new MailingMessage(messageType, text, clientsInfo, destinationDate, user.getId());
        addMailingMessage(message);
    }

    public void prepareAndSendMailingMessages(String messageType, String recipients, String text, String mailingSendDate, String vkType, User user) throws ParseMailingDataException {
        String pattern;
        LocalDateTime destinationDate = LocalDateTime.parse(mailingSendDate, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm МСК"));
        switch (messageType) {
            case "vk":
                addVkMailingToSendQueue(recipients, text, destinationDate, vkType, user);
                return;
            case "slack":
                pattern = SLACK_PATTERN;
                break;
            case "sms":
                pattern = SMS_PATTERN;
                break;
            case "email":
                pattern = EMAIL_PATTERN;
                break;
            default:
                throw new ParseMailingDataException("Incorrect input data for mailing: messageType = " + messageType);
        }
        addMailingToSendQueue(messageType, recipients, text, pattern, destinationDate, user);
    }
}


