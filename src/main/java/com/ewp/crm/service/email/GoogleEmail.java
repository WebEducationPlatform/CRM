package com.ewp.crm.service.email;

import com.ewp.crm.configs.inteface.MailConfig;
import com.ewp.crm.models.Client;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.StatusService;
import com.ewp.crm.utils.converters.IncomeStringToClient;
import org.apache.commons.mail.util.MimeMessageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.mail.ImapIdleChannelAdapter;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.AndTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.FromTerm;
import javax.mail.search.SearchTerm;
import java.util.Optional;
import java.util.Properties;

@Configuration
@EnableIntegration
public class GoogleEmail {

    private String login;
    private String password;
    private String mailFrom;
    private String socketFactoryClass;
    private String socketFactoryFallback;
    private String protocol;
    private String debug;
    private String imapServer;

    private final BeanFactory beanFactory;
    private final ClientService clientService;
    private final StatusService statusService;
    private final IncomeStringToClient incomeStringToClient;


    private static Logger logger = LoggerFactory.getLogger(GoogleEmail.class);

    @Autowired
    public GoogleEmail(MailConfig mailConfig, BeanFactory beanFactory, ClientService clientService, StatusService statusService, IncomeStringToClient incomeStringToClient) {
        this.beanFactory = beanFactory;
        this.clientService = clientService;
        this.statusService = statusService;
        this.incomeStringToClient = incomeStringToClient;

        login = mailConfig.getLogin();
        password = mailConfig.getPassword();
        mailFrom = mailConfig.getMailFrom();
        socketFactoryClass = mailConfig.getSocketFactoryClass();
        socketFactoryFallback = mailConfig.getSocketFactoryFallback();
        protocol = mailConfig.getProtocol();
        debug = mailConfig.getDebug();
        imapServer = mailConfig.getImapServer();
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
        ImapMailReceiver mailReceiver = new ImapMailReceiver("imaps://" + login + ":" + password + "@" + imapServer);
        mailReceiver.setJavaMailProperties(javaMailProperties());
        mailReceiver.setShouldDeleteMessages(false);

        mailReceiver.setShouldMarkMessagesAsRead(true);
        mailReceiver.setCancelIdleInterval(3600);
        mailReceiver.setBeanFactory(beanFactory);
        mailReceiver.setSearchTermStrategy(this::fromAndNotSeenTerm);
        mailReceiver.afterPropertiesSet();

        ImapIdleChannelAdapter imapIdleChannelAdapter = new ImapIdleChannelAdapter(mailReceiver);
        imapIdleChannelAdapter.setAutoStartup(true);
        imapIdleChannelAdapter.setReconnectDelay(300000);
        imapIdleChannelAdapter.setShouldReconnectAutomatically(true);
        imapIdleChannelAdapter.setOutputChannel(directChannel());
        imapIdleChannelAdapter.afterPropertiesSet();

        return imapIdleChannelAdapter;
    }

    @Bean
    public DirectChannel directChannel() {
        DirectChannel directChannel = new DirectChannel();
        directChannel.subscribe(new MessageHandler() {
            public void handleMessage(Message<?> message) throws MessagingException {
                MimeMessageParser parser = new MimeMessageParser((MimeMessage) message.getPayload());
                try {
                    parser.parse();
                    Client client = incomeStringToClient.convert(parser.getPlainContent() != null ? parser.getPlainContent() : parser.getHtmlContent());
                    if (client != null) {
                        client.setStatus(statusService.get(1L));
                        clientService.addClient(client);
                    }
                } catch (Exception e) {
                    logger.error("MimeMessageParser can't parse income data");
                }
            }
        });
        return directChannel;
    }

    private SearchTerm fromAndNotSeenTerm(Flags supportedFlags, Folder folder) {
        Optional<InternetAddress> internetAddress = Optional.empty();
        try {
            internetAddress = Optional.of(new InternetAddress(mailFrom));
        } catch (AddressException e) {
            logger.error("Can't parse email address \"from\"");
        }
        FromTerm fromTerm = new FromTerm(internetAddress.orElse(new InternetAddress()));
        return new AndTerm(fromTerm, new FlagTerm(new Flags(Flags.Flag.SEEN), false));
    }
}
