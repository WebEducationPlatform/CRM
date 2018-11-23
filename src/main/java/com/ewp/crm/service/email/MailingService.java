package com.ewp.crm.service.email;

import com.ewp.crm.models.ClientData;
import com.ewp.crm.models.MailingMessage;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
//@EnableAsync
public class MailingService {
    private static Logger logger = LoggerFactory.getLogger(MailSendServiceImpl.class);
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


    public void sendMessage(MailingMessage message) {
        if (message.getType().equals("email")) {
            sendingMailingsEmails(message);
        } else if (message.getType().equals("sms")) {
            sendingMailingSMS(message);
        } else if (message.getType().equals("vk")) {
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
                final Context ctx = new Context();
                String templateText = message.getText().replaceAll("\n", "");
                ctx.setVariable("templateText", templateText);
                StringBuilder htmlContent = new StringBuilder(htmlTemplateEngine.process("emailStringTemplate", ctx));
                mimeMessageHelper.setText(htmlContent.toString(), true);
                Pattern pattern = Pattern.compile("(?<=cid:)\\S*(?=\\|)");
                Matcher matcher = pattern.matcher(message.getText());
                while (matcher.find()) {
                    String path = ("target/classes/static" + matcher.group()).replaceAll("/", "\\" + File.separator);
                    InputStreamSource inputStreamSource = new FileSystemResource(new File(path));
                    mimeMessageHelper.addInline(matcher.group(), inputStreamSource, "image/jpeg");
                }
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
                vkService.sendMessageById(Long.parseLong(idVk.getInfo()), message.getText());
            } catch (ClassCastException e) {
                logger.info("bad vk id, " + idVk + ", ", e);
            }
        }
        message.setReadedMessage(true);
        mailingMessageRepository.save(message);
    }
}
