package com.ewp.crm.service.email;

import com.ewp.crm.models.ClientData;
import com.ewp.crm.models.MailingMessage;
import com.ewp.crm.models.Message;
import com.ewp.crm.repository.interfaces.MailingMessageRepository;
import com.ewp.crm.service.impl.VKService;
import com.ewp.crm.service.interfaces.SMSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


@Service
//@EnableAsync
public class MailingService {
    private static Logger logger = LoggerFactory.getLogger(MailSendService.class);
    private final JavaMailSender javaMailSender;
    private final SMSService smsService;
    private final VKService vkService;
    private final MailingMessageRepository mailingMessageRepository;

    @Autowired
    public MailingService(SMSService smsService, VKService vkService, JavaMailSender javaMailSender, MailingMessageRepository mailingMessageRepository) {
        this.smsService = smsService;
        this.vkService = vkService;
        this.javaMailSender = javaMailSender;
        this.mailingMessageRepository = mailingMessageRepository;
    }

    public MailingMessage addMailingMessage(MailingMessage message) {
       return mailingMessageRepository.saveAndFlush(message);
    }


    public void sendMessage(MailingMessage message) {
        if(message.getType().equals("email")) {
            sendingMailingsEmails(message);
        } else if(message.getType().equals("sms")) {
            sendingMailingSMS(message);
        } else if(message.getType().equals("vk")) {
            sendingMailingVk(message);
        }
    }

    private void sendingMailingsEmails(MailingMessage message) {
        try {
            final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            final MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            for (ClientData email : message.getClientsData()) {
                mimeMessageHelper.setFrom("Java-Mentor.ru");
                mimeMessageHelper.setTo(email.getInfo());
                mimeMessageHelper.setSubject("Ваш личный Java наставник");
                mimeMessageHelper.setText(message.getText(), true);
                javaMailSender.send(mimeMessage);
            }
            message.setReadedMessage(true);
            mailingMessageRepository.save(message);
        } catch (MessagingException e) {
            logger.info("message no sent ", e);
        } catch (NullPointerException e) {
            logger.info("there is nowhere to send, clientData is empty ", e);
        }
    }

    private void sendingMailingSMS(MailingMessage message) {
        smsService.sendSMS(message.getClientsData(), message.getText());
        message.setReadedMessage(true);
        mailingMessageRepository.save(message);
    }

    private void sendingMailingVk(MailingMessage message) {
        for (ClientData idVk : message.getClientsData()) {
            try {
                long id = Long.parseLong(idVk.getInfo());
                vkService.sendMessageById(id, message.getText());
            } catch (ClassCastException e) {
                logger.info("bad vk id, " + idVk +", ", e);
            }
        }
        message.setReadedMessage(true);
        mailingMessageRepository.save(message);
    }
}
