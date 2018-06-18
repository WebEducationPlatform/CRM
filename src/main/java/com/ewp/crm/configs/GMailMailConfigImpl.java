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

    private static Logger logger = LoggerFactory.getLogger(GMailMailConfigImpl.class);

    @Autowired
    public GMailMailConfigImpl(Environment env) {
        login = env.getProperty("google.mail.login").replaceAll("@", "%40");
        password = env.getProperty("google.mail.password");
        mailFrom = env.getProperty("mail.from");
        socketFactoryClass = env.getProperty("mail.imap.socketFactory.class");
        socketFactoryFallback = env.getProperty("mail.imap.socketFactory.fallback");
        protocol = env.getProperty("mail.store.protocol");
        debug = env.getProperty("mail.debug");
        imapServer = env.getProperty("mail.imap.server");
        if (!configIsValid()) {
            logger.error("GMail configs have not initialized. Check gmail.properties file");
            System.exit(-1);
        }
    }

    private boolean configIsValid() {
        if (login == null || "".equals(login)) return false;
        if (password == null || "".equals(password)) return false;
        if (mailFrom == null || "".equals(mailFrom)) return false;
        if (socketFactoryClass == null || "".equals(socketFactoryClass)) return false;
        if (socketFactoryFallback == null || "".equals(socketFactoryFallback)) return false;
        if (protocol == null || "".equals(protocol)) return false;
        if (debug == null || "".equals(debug)) return false;
        if (imapServer == null || "".equals(imapServer)) return false;
        return true;
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
}
