package com.ewp.crm.configs;

import com.ewp.crm.configs.inteface.MailConfig;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.MessageTemplate;
import com.ewp.crm.models.ProjectProperties;
import com.ewp.crm.service.interfaces.*;
import com.ewp.crm.util.converters.IncomeStringToClient;
import org.apache.commons.mail.util.MimeMessageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.mail.ImapIdleChannelAdapter;
import org.springframework.integration.mail.ImapMailReceiver;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.*;
import java.util.Optional;
import java.util.Properties;

@Configuration
@EnableIntegration
public class GoogleEmailConfig {

    private String login;
    private String password;
    private String mailFrom;
    private String socketFactoryClass;
    private String socketFactoryFallback;
    private String protocol;
    private String debug;
    private String imapServer;
    private String mailJavaLearn;

    private final BeanFactory beanFactory;
    private final ClientService clientService;
    private final StatusService statusService;
    private final IncomeStringToClient incomeStringToClient;
    private final ClientHistoryService clientHistoryService;
    private final MailSendService prepareAndSend;
    private final ProjectPropertiesService projectPropertiesService;
    private final SendNotificationService sendNotificationService;
    
    private static Logger logger = LoggerFactory.getLogger(GoogleEmailConfig.class);
    private final Environment env;

    @Autowired
    public GoogleEmailConfig(MailSendService prepareAndSend, MailConfig mailConfig, BeanFactory beanFactory,
                             ClientService clientService, StatusService statusService,
                             IncomeStringToClient incomeStringToClient, ClientHistoryService clientHistoryService,
                             ProjectPropertiesService projectPropertiesService,
                             SendNotificationService sendNotificationService, Environment env) {
        this.beanFactory = beanFactory;
        this.clientService = clientService;
        this.statusService = statusService;
        this.incomeStringToClient = incomeStringToClient;
        this.prepareAndSend = prepareAndSend;
        this.sendNotificationService = sendNotificationService;

        login = mailConfig.getLogin();
        password = mailConfig.getPassword();
        mailFrom = mailConfig.getMailFrom();
        socketFactoryClass = mailConfig.getSocketFactoryClass();
        socketFactoryFallback = mailConfig.getSocketFactoryFallback();
        protocol = mailConfig.getProtocol();
        debug = mailConfig.getDebug();
        imapServer = mailConfig.getImapServer();
        mailJavaLearn = mailConfig.getMailJavalearn();
        this.clientHistoryService = clientHistoryService;
        this.projectPropertiesService = projectPropertiesService;
        this.env = env;
    }

    private Properties javaMailProperties() {
        Properties javaMailProperties = new Properties();
        javaMailProperties.setProperty("mail.imap.socketFactory.class", socketFactoryClass);
        javaMailProperties.setProperty("mail.imap.socketFactory.fallback", socketFactoryFallback);
        javaMailProperties.setProperty("mail.store.protocol", protocol);
        javaMailProperties.setProperty("mail.debug", debug);

        return javaMailProperties;
    }

    @Bean
    public ImapIdleChannelAdapter mailAdapter() {
        // Если стоит пароль по умолчанию * (из gmail.properties), то не зпускаем службу слежки за почтой
        if (password.equals("*")) return null;
        
        ImapMailReceiver mailReceiver = new ImapMailReceiver("imaps://" + login + ":" + password + "@" + imapServer);
        mailReceiver.setJavaMailProperties(javaMailProperties());
        mailReceiver.setShouldDeleteMessages(false);

        mailReceiver.setShouldMarkMessagesAsRead(true);
        mailReceiver.setCancelIdleInterval(300);
        mailReceiver.setBeanFactory(beanFactory);
        mailReceiver.setSearchTermStrategy(this::fromAndNotSeenTerm);
        mailReceiver.afterPropertiesSet();

        ImapIdleChannelAdapter imapIdleChannelAdapter = new ImapIdleChannelAdapter(mailReceiver);
        imapIdleChannelAdapter.setAutoStartup(true);
        imapIdleChannelAdapter.setShouldReconnectAutomatically(true);
        imapIdleChannelAdapter.setOutputChannel(directChannel());
        imapIdleChannelAdapter.afterPropertiesSet();

        return imapIdleChannelAdapter;
    }
    
    @Bean
    public DirectChannel directChannel() {
        DirectChannel directChannel = new DirectChannel();
        directChannel.subscribe(message -> {
            boolean sendAutoAnswer = true; // TODO Убрать костыль.
            ProjectProperties properties = projectPropertiesService.getOrCreate();
            MessageTemplate template = properties.getAutoAnswerTemplate();
            try {
                MimeMessageParser parser = new MimeMessageParser(new MimeMessage((MimeMessage) message.getPayload()));
                try {
                    parser.parse();
                    Optional<Client> clientOpt = incomeStringToClient.convert(parser.getHtmlContent());
                    if (clientOpt.isPresent()) {
                        Client client = clientOpt.get();
                        if (parser.getHtmlContent().contains("Java Test")) {
                            prepareAndSend.validatorTestResult(parser.getPlainContent(), client);
                        }
                        clientHistoryService.createHistory("GMail").ifPresent(client::addHistory);
                        if (client.getClientDescriptionComment().equals(env.getProperty("messaging.client.description.java-learn-link"))) {
                            sendAutoAnswer = false; // Для клиентов из javalearn временно отключаем автоответ
                            statusService.get("Постоплата2").ifPresent(client::setStatus);
                        } else {
                            sendAutoAnswer = true;
                            statusService.getFirstStatusForClient().ifPresent(client::setStatus);
                        }
                        clientService.addClient(client);
                        sendNotificationService.sendNewClientNotification(client, "gmail");
                        if (sendAutoAnswer && template != null) {
                            prepareAndSend.sendEmailInAllCases(client);
                        } else {
                            logger.info("E-mail auto-answer has been set to OFF");
                        }
                    }
                } catch (Exception e) {
                    logger.error("MimeMessageParser can't parse income data ", e);
                }
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });
        return directChannel;
    }

    private SearchTerm fromAndNotSeenTerm(Flags supportedFlags, Folder folder) {
        Optional<InternetAddress> internetAddress = Optional.empty();
        Optional<InternetAddress> javaLearnAddress = Optional.empty();
        try {
            internetAddress = Optional.of(new InternetAddress(mailFrom));
            javaLearnAddress = Optional.of(new InternetAddress(mailJavaLearn));
        } catch (AddressException e) {
            logger.error("Can't parse email address \"from\"", e);
        }
        FromTerm fromTerm = new FromTerm(internetAddress.orElse(new InternetAddress()));
        FromTerm fromJavaLearnTerm = new FromTerm(javaLearnAddress.orElse(new InternetAddress()));
        return new AndTerm(new OrTerm(fromTerm, fromJavaLearnTerm), new FlagTerm(new Flags(Flags.Flag.SEEN), false));
    }
}
