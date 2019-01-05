package com.ewp.crm.service.email;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.dto.MailDto;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.MailReceiverService;
import com.ewp.crm.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;


@Service
@PropertySource("classpath:application.properties")
public class MailReceiverServiceImpl implements MailReceiverService {

    private static Logger logger = LoggerFactory.getLogger(MailReceiverServiceImpl.class);

    private String eMailServerHost;
    private String eMailLogin;
    private String eMailPassword;
    private Environment environment;
    private UserService userService;
    private MailDto mailDto;
    private ClientService clientService;


    @Autowired
    public MailReceiverServiceImpl(Environment environment,
                                   UserService userService,
                                   MailDto mailDto,
                                   ClientService clientService) {
        this.clientService = clientService;
        this.mailDto = mailDto;
        this.userService = userService;
        checkConfig(environment);
    }

    private void checkConfig(Environment environment) {
        try {
            this.eMailServerHost = environment.getRequiredProperty("spring.mail.host");
            this.eMailLogin = environment.getRequiredProperty("spring.mail.username");
            this.eMailPassword = environment.getRequiredProperty("spring.mail.password");

            if (eMailServerHost.isEmpty() || eMailLogin.isEmpty() || eMailPassword.isEmpty())
                throw new NullPointerException();

        } catch (IllegalStateException | NullPointerException e) {
            logger.error("Mail configs failed to get initialized in MailReceiverService. Check application.properties file");
            System.exit(-1);
        }
    }

    @Override
    public List<Long> checkMessagesInGMailInbox() {
        List<Long> userIdList = new ArrayList<>();

        Message[] messages = getMessages("imaps", "imap.gmail.com", eMailLogin,
                eMailPassword, "INBOX");

        if (messages != null) {
            for (Message message : messages) {
                try {
                    if (!message.isSet(Flags.Flag.SEEN)) {
                        String email = getEmailAddress(message.getFrom()[0].toString());
                        try {
                            long clientId = clientService.getClientByEmail(email).getId();
                            userIdList.add(clientId);
                            message.setFlag(Flags.Flag.SEEN, false);
                        } catch (NullPointerException e) {
                            logger.error("Email from unknown address {} has been received ", email);
                        }
                    }
                } catch (MessagingException e) {
                    logger.error("Messaging exception" + e);
                }
            }
            return userIdList;
        }
        logger.error("List of messages is null in checkMessagesInGMailInbox()");
        return null;
    }

    @Override
    public List<MailDto> getAllUnreadEmailsFor(Long id)  {
        Client client;
        MailDto mail = new MailDto();
        List<MailDto> messageList = new ArrayList<>();

        Message [] messages = getMessages("imaps", "imap.gmail.com", eMailLogin,
                eMailPassword, "INBOX");

            for (Message message : messages){
                try {
                    if (!message.isSet(Flags.Flag.SEEN)){
                        String email = getEmailAddress(message.getFrom()[0].toString());
                        try {
                            client = clientService.getClientByEmail(email);

                            if (client.getId() == id){
                                mail.setUserId(client.getId());
                                mail.setSentFrom(email);
                                mail.setSentDate(convertDate(message.getSentDate()));
                                mail.setContent(getTextFromMessage(message));
                                mail.setSubject(message.getSubject());
                                messageList.add(mail);
                            } else
                                message.setFlag(Flags.Flag.SEEN, false);

                        }catch (NullPointerException e){
                            logger.error("Email from unknown address {} has been received ", email);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        return messageList;
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

    private String getTextFromMimeMultipart(MimeMultipart mimeMultipart)  throws MessagingException, IOException{
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break;
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
            } else if (bodyPart.getContent() instanceof MimeMultipart){
                result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
            }
        }
        return result;
    }

    private String convertDate(Date date){
        Format formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return formatter.format(date);
    }

    private String getEmailAddress(String email){
        return email.substring(email.indexOf("\" <")+3, email.lastIndexOf(">"));
    }

    private Message[] getMessages(String protocol, String host, String eMailLogin,
                                  String eMailPassword, String mailboxToOpen){
        Store store;
        Folder inbox;
        Message[] messages = null;
        Properties properties = new Properties();
        Session session = Session.getDefaultInstance(properties, null);

        try {
            store = session.getStore(protocol);
            store.connect(host, eMailLogin, eMailPassword);
            inbox = store.getFolder(mailboxToOpen);
            inbox.open(Folder.READ_WRITE);
            messages = inbox.getMessages();
        } catch (NoSuchProviderException e) {
            logger.error("Failed to connect to remote server, wrong protocol" + e);
        } catch (MessagingException e) {
            logger.error("Messaging exception in getMessage()" + e);
        }
        return messages;
    }
}
