package com.ewp.crm.configs;

import com.ewp.crm.configs.inteface.MailConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource("file:./gmail.properties")
public class GMailMailConfigImpl implements MailConfig {

    private String login;
    private String password;
    private String mailFrom;
    private String socketFactoryClass;
    private String socketFactoryFallback;
    private String protocol;
    private String debug;
    private String imapServer;
    private String mailJavalearn;
    private String mailBootCamp;

    private static Logger logger = LoggerFactory.getLogger(GMailMailConfigImpl.class);

    @Autowired
    public GMailMailConfigImpl(Environment env) {
        try {
            login = env.getRequiredProperty("google.mail.login").replaceAll("@", "%40");
            password = env.getRequiredProperty("google.mail.password");
            mailFrom = env.getRequiredProperty("mail.from");
            socketFactoryClass = env.getRequiredProperty("mail.imap.socketFactory.class");
            socketFactoryFallback = env.getRequiredProperty("mail.imap.socketFactory.fallback");
            protocol = env.getRequiredProperty("mail.store.protocol");
            debug = env.getRequiredProperty("mail.debug");
            imapServer = env.getRequiredProperty("mail.imap.server");
            mailJavalearn = env.getRequiredProperty("mail.javaLearn");
            mailBootCamp = env.getRequiredProperty("mail.bootCamp");
            if (login.isEmpty() || password.isEmpty() || mailFrom.isEmpty() || socketFactoryClass.isEmpty() ||
                    socketFactoryFallback.isEmpty() || protocol.isEmpty() ||
                    debug.isEmpty() || imapServer.isEmpty() || mailJavalearn.isEmpty() || mailBootCamp.isEmpty()) {
                throw new NoSuchFieldException();
            }
        } catch (IllegalStateException | NoSuchFieldException e) {
            logger.error("GMail configs have not been initialized. Check gmail.properties file", e);
            System.exit(1);
        }
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public String getSocketFactoryClass() {
        return socketFactoryClass;
    }

    public String getSocketFactoryFallback() {
        return socketFactoryFallback;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getDebug() {
        return debug;
    }

    public String getImapServer() {
        return imapServer;
    }

    public String getMailJavalearn() {
        return mailJavalearn;
    }

    public String getMailBootCamp() {
        return mailBootCamp;
    }
}
