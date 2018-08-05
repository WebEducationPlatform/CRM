package com.ewp.crm.service.email;

import com.ewp.crm.service.impl.VKService;
import com.ewp.crm.configs.inteface.MailConfig;
import com.ewp.crm.models.Client;
import com.ewp.crm.service.interfaces.ClientHistoryService;
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


import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.AndTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.FromTerm;
import javax.mail.search.SearchTerm;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private final ClientHistoryService clientHistoryService;
    private final MailSendService prepareAndSend;


    private static Logger logger = LoggerFactory.getLogger(GoogleEmail.class);

    @Autowired
    public GoogleEmail(MailSendService prepareAndSend, MailConfig mailConfig, BeanFactory beanFactory, ClientService clientService, StatusService statusService, IncomeStringToClient incomeStringToClient, ClientHistoryService clientHistoryService, VKService vkService) {
        this.beanFactory = beanFactory;
        this.clientService = clientService;
        this.statusService = statusService;
        this.incomeStringToClient = incomeStringToClient;
        this.prepareAndSend = prepareAndSend;

        login = mailConfig.getLogin();
        password = mailConfig.getPassword();
        mailFrom = mailConfig.getMailFrom();
        socketFactoryClass = mailConfig.getSocketFactoryClass();
        socketFactoryFallback = mailConfig.getSocketFactoryFallback();
        protocol = mailConfig.getProtocol();
        debug = mailConfig.getDebug();
        imapServer = mailConfig.getImapServer();
	    this.clientHistoryService = clientHistoryService;
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
            MimeMessageParser parser = new MimeMessageParser((MimeMessage) message.getPayload());
            try {
                logger.info("start parsing income email", parser.getHtmlContent());
                parser.parse();
                Client client = incomeStringToClient.convert(parser.getHtmlContent());
                if (client != null) {
                    if (parser.getHtmlContent().contains("Java Test")) {
                        Double list = validatorTestResult(parser.getPlainContent());
                        prepareAndSend.sendMail("Java-Mentor.ru", client.getEmail(), "Test complete!",  String.format("Результаты пройденого теста \n\nпроцент правильных ответов %1$,.0f", list));
                    }
                    client.setStatus(statusService.get(1L));
                    client.addHistory(clientHistoryService.createHistory("GMail"));
                    clientService.addClient(client);
                }
            } catch (Exception e) {
                logger.error("MimeMessageParser can't parse income data ", e);
            }
        });
        return directChannel;
    }

    private Double validatorTestResult(String parseContent) {
        Pattern pattern2 = Pattern.compile("\\d[:]\\s\\d\\s");
        Matcher m = pattern2.matcher(parseContent);

        Map<Integer, Integer> rightAnswer = new HashMap<>();
        rightAnswer.put(1, 2);
        rightAnswer.put(2, 1);
        rightAnswer.put(3, 3);
        rightAnswer.put(4, 2);
        rightAnswer.put(5, 3);
        rightAnswer.put(6, 4);

        Map<Integer, Integer> user = new HashMap<>();
        while (m.find()) {
            String tmp = m.group();
            String index = tmp.substring(0, tmp.indexOf(":"));
            tmp = tmp.replaceAll("([0-9][:])|\\s", "");
            user.put(Integer.valueOf(index), Integer.valueOf(tmp));
        }

        int countOfRight = 0;
        for(Map.Entry<Integer, Integer> map : user.entrySet()) {
            boolean ans = map.getValue().equals(rightAnswer.get(map.getKey()));
            if(ans) {
                countOfRight++;
            }
        }

        double allAnswer = rightAnswer.size();
        double procentOfRigthAnswers = (countOfRight / allAnswer) * 100;
        return procentOfRigthAnswers;
    }

    private SearchTerm fromAndNotSeenTerm(Flags supportedFlags, Folder folder) {
        Optional<InternetAddress> internetAddress = Optional.empty();
        try {
            internetAddress = Optional.of(new InternetAddress(mailFrom));
        } catch (AddressException e) {
            logger.error("Can't parse email address \"from\"", e);
        }
        FromTerm fromTerm = new FromTerm(internetAddress.orElse(new InternetAddress()));
        return new AndTerm(fromTerm, new FlagTerm(new Flags(Flags.Flag.SEEN), false));
    }
}
