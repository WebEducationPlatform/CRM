package com.ewp.crm.service.email;

import com.ewp.crm.exceptions.email.MessageTemplateException;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.MessageSubject;
import com.ewp.crm.models.MessageTemplate;
import com.ewp.crm.models.Status;
import com.ewp.crm.service.impl.MessageSubjectServiceImpl;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.MailAutoAnswerService;
import com.ewp.crm.service.interfaces.MessageSubjectService;
import com.ewp.crm.service.interfaces.MessageTemplateService;
import com.google.common.collect.Streams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.mail.*;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;


@Service
@PropertySources(value = {@PropertySource("classpath:application.properties")})
public class MailAutoAnswerServiceImpl implements MailAutoAnswerService {

    private MessageSubjectService messageSubjectService;
    private ClientService clientService;
    private MessageTemplateService messageTemplateService;

    private Environment environment;
    private static String defaultTemplate = "Не известный";
    private static String username = null;
    private static String password = null;

    private Properties properties = new Properties();

    public MailAutoAnswerServiceImpl(Environment environment,
                                     MessageSubjectService messageSubjectService,
                                     MessageTemplateService messageTemplateService,
                                     ClientService clientService) {
        this.environment = environment;
        this.messageSubjectService = messageSubjectService;
        this.clientService = clientService;
        this.messageTemplateService = messageTemplateService;
        username = environment.getProperty("spring.mail.username");
        password = environment.getProperty("spring.mail.password");

    }

    @PostConstruct
    private void initProperties() {
//        properties.put("mail.debug", "true");
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imaps.host", "imap.gmail.com");
        properties.put("mail.imaps.port", "993");
        properties.put("mail.imaps.timeout", "10000");

        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true"); //TLS
        receiveAndSendEmails();
    }

    public void receiveAndSendEmails() {

        Session session = Session.getInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        IMAPStore store = null;
        Folder inbox = null;

        try {

            store = (IMAPStore) session.getStore("imaps");
            store.connect();

            if (!store.hasCapability("IDLE")) {
                throw new RuntimeException("IDLE not supported");
            }

            inbox = store.getFolder("INBOX");
            inbox.addMessageCountListener(new MessageCountAdapter() {

                @Override
                public void messagesAdded(MessageCountEvent event) {
                    Message[] messages = event.getMessages();
                    for (Message message : messages) {

                        try {
                            Message newMessage = parseMessageAndCreateAnswer(message, session);
                            Transport.send(newMessage);
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            ExecutorService executorService = Executors.newFixedThreadPool(10);
            IdleThread idleThread = new IdleThread(inbox);
            idleThread.setDaemon(false);
            executorService.execute(idleThread);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    private static class IdleThread extends Thread {

        private final Folder folder;
        private volatile boolean running = true;

        public IdleThread(Folder folder) {
            super();
            this.folder = folder;
        }

        public synchronized void kill() {

            if (!running)
                return;
            this.running = false;
        }

        @Override
        public void run() {
            while (running) {

                try {
                    ensureOpen(folder);
                    ((IMAPFolder) folder).idle();
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(50000);
                    } catch (InterruptedException e1) {
                        // ignore
                    }
                }

            }
        }
    }

    public static void close(final Folder folder) {
        try {
            if (folder != null && folder.isOpen()) {
                folder.close(false);
            }
        } catch (final Exception e) {
            // ignore
        }

    }

    public static void close(final Store store) {
        try {
            if (store != null && store.isConnected()) {
                store.close();
            }
        } catch (final Exception e) {
            // ignore
        }

    }

    public static void ensureOpen(final Folder folder) throws MessagingException {

        if (folder != null) {
            Store store = folder.getStore();
            if (store != null && !store.isConnected()) {
                store.connect(username, password);
            }
        } else {
            throw new MessagingException("Unable to open a null folder");
        }

        if (folder.exists() && !folder.isOpen() && (folder.getType() & Folder.HOLDS_MESSAGES) != 0) {
            folder.open(Folder.READ_ONLY);
            if (!folder.isOpen())
                throw new MessagingException("Unable to open folder " + folder.getFullName());
        }

    }

    private Message parseMessageAndCreateAnswer(Message message, Session session) throws MessagingException {
        Address[] from = message.getFrom();
        String titleFromIncomingMessage = message.getSubject();
        String templateForAnswer = "";
        Client client = null;

        Optional<MessageSubject> messageSubjectServiceByTitle =
                messageSubjectService.getByTitle(titleFromIncomingMessage);
        if (messageSubjectServiceByTitle.isPresent()) {
            templateForAnswer = messageSubjectServiceByTitle.get().getMessageTemplate().getTemplateText();
        } else {
            Optional<MessageSubject> defaultSubjectTemplate = messageSubjectService.getByTitle(defaultTemplate);
            if (defaultSubjectTemplate.isPresent()) {
                templateForAnswer = defaultSubjectTemplate.get().getMessageTemplate().getTemplateText();

            } else {
                throw new MessageTemplateException("Unknown title: " + titleFromIncomingMessage);
            }
        }

        try {
            client = parseClientAndAddIntoStatus(message);

        } catch (IOException e) {
            e.printStackTrace();
        }

        Message newMessage = new MimeMessage(session);
        newMessage.setFrom(new InternetAddress(client.getEmail().get().trim()));
        newMessage.setRecipient(Message.RecipientType.TO,
                new InternetAddress(from[0].toString()));

        newMessage.setSubject("Java Mentor");

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();

        templateForAnswer = templateForAnswer.replace("%fullName%", client.getName())
                .replace("%dateOfSkypeCall%", dateFormat.format(date))
                .replace("%bodyText%","");

        newMessage.setText(templateForAnswer);

        return newMessage;
    }

    public Client parseClientAndAddIntoStatus(Message message)
            throws IOException, MessagingException {

        String textFromMessage = getTextFromMessage(message);
        String subject = message.getSubject();
        Status status;

        Optional<MessageSubject> messageSubjectServiceByTitle = messageSubjectService.getByTitle(subject);
        if (messageSubjectServiceByTitle.isPresent()) {
            status = messageSubjectServiceByTitle.get().getStatus();
        } else {
            Optional<MessageSubject> defaultMessageSubject = messageSubjectService.getByTitle(defaultTemplate);
            if (defaultMessageSubject.get().getStatus() != null) {
                status = defaultMessageSubject.get().getStatus();
            } else {
                status = null;
            }

        }

        String[] split = textFromMessage.split("\n");
        Map<String, String> map = new HashMap<>();

        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].trim();

            if (!split[i].isEmpty()) {
                if (split[i].contains(":")) {
                    int index = split[i].indexOf(":");
                    String key = split[i].substring(0, index);
                    String key_upper = key.toUpperCase();
                    if (key_upper.contains("NAME") || key_upper.contains("ИМЯ")) {
                        map.put("Name", split[i].substring(index + 1));
                    } else if (key_upper.contains("PHONE") || key_upper.contains("ТЕЛЕФОН")) {
                        map.put("Phone", split[i].substring(index + 1));
                    }
                    if (key_upper.contains("EMAIL") || key_upper.contains("ПОЧТА")) {
                        map.put("Email", split[i].substring(index + 1));
                    }
                }

            }
        }

        Client.Builder clientBuilder =
                new Client.Builder(map.get("Name"), map.get("Phone"), map.get("Email"));
        Client client = clientBuilder.build();

        if (status != null) {
            client.setStatus(status);
            clientService.addClient(client);
        }

        return client;
    }

    private String getTextFromMessage(Message message) throws MessagingException, IOException {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    private String getTextFromMimeMultipart(
            MimeMultipart mimeMultipart) throws MessagingException, IOException {
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break; // without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
            }
        }
        return result;
    }

}


