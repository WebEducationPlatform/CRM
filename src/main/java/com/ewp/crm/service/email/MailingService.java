package com.ewp.crm.service.email;


import com.ewp.crm.models.ClientData;
import com.ewp.crm.models.MailingMessage;
import com.ewp.crm.models.User;
import com.ewp.crm.repository.interfaces.MailingMessageRepository;
import com.ewp.crm.service.interfaces.VKService;
import com.ewp.crm.service.interfaces.SMSService;
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
    private static final String emailPattern = "\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}\\b";
    private static final String smsPattern = "\\d{11}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}";

    private final JavaMailSender javaMailSender;
    private final SMSService smsService;
    private final VKService vkService;
    private final MailingMessageRepository mailingMessageRepository;
    private final TemplateEngine htmlTemplateEngine;

    @Autowired
    public MailingService(SMSService smsService, VKService vkService, JavaMailSender javaMailSender,
                          MailingMessageRepository mailingMessageRepository, TemplateEngine htmlTemplateEngine) {
        this.smsService = smsService;
        this.vkService = vkService;
        this.javaMailSender = javaMailSender;
        this.mailingMessageRepository = mailingMessageRepository;
        this.htmlTemplateEngine = htmlTemplateEngine;
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
        if (message.getType().equals("email")) {
            result = sendingMailingsEmails(message);
        } else if (message.getType().equals("sms")) {
            sendingMailingSMS(message);
        } else if (message.getType().equals("vk") && message.getVkType().equals("managerPage")) {
            sendingMailingVkWithManagerAccount(message);
        } else {
            sendingMailingVk(message);
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

    private void sendingMailingVk(MailingMessage message) {
        List<String> notSendList = new ArrayList<>();
        for (ClientData idVk : message.getClientsData()) {
            try {
                Thread.sleep(1000);
                String value = vkService.sendMessageById(Long.parseLong(idVk.getInfo()), message.getText(), message.getVkType());
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

    public boolean parseMailingMessages(String type, String templateText, String recipients, String text, String date, boolean sendnow, String vkType, User user) {
        String pattern;
        String template;
        switch (type) {
            case "vk":
                pattern = "";
                template = text;
                break;
            case "sms":
                pattern = smsPattern;
                template = text;
                break;
            case "email":
                pattern = emailPattern;
                template = templateText;
                break;
            default:
                return false;
        }
        LocalDateTime destinationDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm МСК"));
        Set<ClientData> clientsInfo = new HashSet<>();
        if (type.equals("vk")) {
            // Preparing messages
            List<MailingMessage> vkMessages = new ArrayList<>();
            String[] vkIds = recipients.split("\n");
            Arrays.asList(vkIds).forEach(x -> {
                String vkIdentify = vkService.getIdFromLink(x.trim());
                if (vkIdentify != null) {
                    clientsInfo.add(new ClientData(vkIdentify));
                }
            });
            clientsInfo.forEach(c -> vkMessages.add(new MailingMessage(type, template, new HashSet<>(Collections.singletonList(c)), destinationDate, vkType, user.getId())));
            // Adding messages to queue
            if (sendnow) {
                vkMessages.forEach(msg -> {
                    msg.setDate(LocalDateTime.now());
                    addMailingMessage(msg);
                });
            } else {
                vkMessages.forEach(this::addMailingMessage);
            }
        } else {
            // Preparing messages: only get recepients who matches address pattern
            Matcher recepientMatcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(recipients);
            while (recepientMatcher.find()) {
                clientsInfo.add(new ClientData(recepientMatcher.group()));
            }
            // Adding messages to queue
            MailingMessage message = new MailingMessage(type, template, clientsInfo, destinationDate, user.getId());
            if (sendnow) {
                message.setDate(LocalDateTime.now());
                addMailingMessage(message);
            } else {
                addMailingMessage(message);
            }
        }
        return true;
    }
}


