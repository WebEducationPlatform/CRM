package com.ewp.crm.configs;

import com.ewp.crm.configs.inteface.MailConfig;
import com.ewp.crm.models.*;
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
import javax.mail.search.AndTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.FromTerm;
import javax.mail.search.OrTerm;
import javax.mail.search.SearchTerm;
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
    private String mailBootCamp;
	private boolean autoSetUser = false;

    private final BeanFactory beanFactory;
    private final ClientService clientService;
    private final StatusService statusService;
    private final IncomeStringToClient incomeStringToClient;
    private final ClientHistoryService clientHistoryService;
    private final MailSendService prepareAndSend;
    private final ProjectPropertiesService projectPropertiesService;
    private final SendNotificationService sendNotificationService;
    private final UserService userService;
    private final AutoAnswersService autoAnswersService;

    private static Logger logger = LoggerFactory.getLogger(GoogleEmailConfig.class);
    private final Environment env;

    @Autowired
    public GoogleEmailConfig(MailSendService prepareAndSend, MailConfig mailConfig, BeanFactory beanFactory,
                             ClientService clientService, StatusService statusService,
                             IncomeStringToClient incomeStringToClient, ClientHistoryService clientHistoryService,
                             ProjectPropertiesService projectPropertiesService,
                             SendNotificationService sendNotificationService, Environment env,
                             UserService userService, AutoAnswersService autoAnswersService) {
        this.beanFactory = beanFactory;
        this.clientService = clientService;
        this.statusService = statusService;
        this.incomeStringToClient = incomeStringToClient;
        this.prepareAndSend = prepareAndSend;
        this.sendNotificationService = sendNotificationService;
        this.userService = userService;

        login = mailConfig.getLogin();
        password = mailConfig.getPassword();
        mailFrom = mailConfig.getMailFrom();
        mailBootCamp = mailConfig.getMailBootCamp();
        socketFactoryClass = mailConfig.getSocketFactoryClass();
        socketFactoryFallback = mailConfig.getSocketFactoryFallback();
        protocol = mailConfig.getProtocol();
        debug = mailConfig.getDebug();
        imapServer = mailConfig.getImapServer();
        mailJavaLearn = mailConfig.getMailJavalearn();
        this.clientHistoryService = clientHistoryService;
        this.projectPropertiesService = projectPropertiesService;
        this.env = env;
        this.autoAnswersService = autoAnswersService;
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
            boolean addClient = true; // TODO Убрать костыль))
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
                        addClient = !parser.getHtmlContent().contains("javabootcamp.ru"); // не создавать карточку для клиента из заявки на буткемп
                        clientHistoryService.createHistory("GMail").ifPresent(client::addHistory);
                        //get status by subject request
                        Optional<AutoAnswer> autoAnswer = autoAnswersService.getAutoAnswerBySubject(parser.getSubject());
                        if (autoAnswer.isPresent()){
                            Status status = autoAnswer.get().getStatus() != null ? autoAnswer.get().getStatus(): statusService.get("Новые").get();
                            client.setStatus(status);
                            //Auto answer to client by subject get templateMessage
                            if (client.getEmail().isPresent()){
                                MessageTemplate messageTemplate = autoAnswer.get().getMessageTemplate();
                                String subject = messageTemplate.getTheme().length() >0 ?
                                        messageTemplate.getTheme() : env.getProperty("messaging.mailing.set-from-Java-Mentor");
                                prepareAndSend.sendMessage(subject,messageTemplate.getTemplateText(),client.getEmail().get());
                            }
                        }else{

                            if (client.getEmail().isPresent()){
                                prepareAndSend.sendMessage(env.getProperty("messaging.mailing.set-from-Java-Mentor"),
                                        env.getProperty("messaging.mailing.set-message-js-learn-autoanswer"), client.getEmail().get());
                            }
                        }
                        if (addClient) {
							if (autoSetUser) {
								userService.getUserToOwnCard().ifPresent(client::setOwnerUser);
							}
                            UserRoutes.UserRouteType routeType = null;
                            if (parser.getSubject().contains("java-mentor")){
                                routeType = UserRoutes.UserRouteType.FROM_JM_EMAIL;
                            }
                            userService.getUserToOwnCard(routeType).ifPresent(client::setOwnerUser);

                            clientService.addClient(client, null);
                            if (parser.getSubject().contains("java-mentor")) {
                                sendNotificationService.sendNewClientNotification(client, "Java-mentor");
                            } else if (parser.getSubject().contains("javalearn")) {
                                sendNotificationService.sendNewClientNotification(client, "Java-learn");
                            } else if (parser.getSubject().contains("jslearn")) {
                                sendNotificationService.sendNewClientNotification(client, "JS-learn");
                            } else {
                                sendNotificationService.sendNewClientNotification(client, "gmail");
                            }
                            if (sendAutoAnswer && template != null) {
                                prepareAndSend.sendEmailInAllCases(client);
                            } else {
                                logger.info("E-mail auto-answer has been set to OFF");
                            }
                        } else {
                            if (client.getEmail().isPresent()) {
                                logger.info("Got request from javabootcamp, sending auto answer to " + client.getEmail().get());
                                prepareAndSend.sendMessage(env.getProperty("messaging.mailing.set-subject-bootcamp-autoanswer"), env.getProperty("messaging.mailing.set-message-bootcamp-autoanswer"), client.getEmail().get());
                            } else {
                                logger.info("No email found when parsing request from javabootcamp.ru: " + parser.getHtmlContent());
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("MimeMessageParser can't parse income data ", e);
                }
            } catch (MessagingException e) {
                logger.error("MimeMessageParser can't get message ", e);
            }
        });
        return directChannel;
    }

    private SearchTerm fromAndNotSeenTerm(Flags supportedFlags, Folder folder) {
        Optional<InternetAddress> internetAddress = Optional.empty();
        Optional<InternetAddress> javaLearnAddress = Optional.empty();
        Optional<InternetAddress> bootCampAddress = Optional.empty();
        try {
            internetAddress = Optional.of(new InternetAddress(mailFrom));
            javaLearnAddress = Optional.of(new InternetAddress(mailJavaLearn));
            bootCampAddress = Optional.of(new InternetAddress(mailBootCamp));
        } catch (AddressException e) {
            logger.error("Can't parse email address \"from\"", e);
        }
        FromTerm fromTerm = new FromTerm(internetAddress.orElse(new InternetAddress()));
        FromTerm fromJavaLearnTerm = new FromTerm(javaLearnAddress.orElse(new InternetAddress()));
        FromTerm fromBootCampTerm = new FromTerm(bootCampAddress.orElse(new InternetAddress()));
        return new AndTerm(new OrTerm(new FromTerm[]{fromTerm, fromJavaLearnTerm, fromBootCampTerm}), new FlagTerm(new Flags(Flags.Flag.SEEN), false));
    }
}
