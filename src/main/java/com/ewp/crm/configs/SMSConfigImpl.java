package com.ewp.crm.configs;

import com.ewp.crm.configs.inteface.SMSConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Configuration
@PropertySource("file:./sms.properties")
public class SMSConfigImpl implements SMSConfig {

    private String login;
    private String password;
    private String alphaName;
    private static Logger logger = LoggerFactory.getLogger(SMSConfigImpl.class);

    @Autowired
    public SMSConfigImpl(Environment env) {
        try {
            login = env.getRequiredProperty("sms.login");
            password = env.getRequiredProperty("sms.password");
            alphaName = env.getRequiredProperty("sms.alpha-name");
            if (login.isEmpty() || password.isEmpty() || alphaName.isEmpty()) {
                throw new NoSuchFieldException();
            }
        } catch (IllegalStateException | NoSuchFieldException e) {
            logger.error("Sms config hasn't been initialized. Check sms.properties file", e);
        }
    }

    @PostConstruct
    private void checkInitializeProperties() {
        if (login.equals("*") ||
                password.equals("*") ||
                alphaName.equals("*")) {
            logger.error("Sms config hasn't been initialized. Check sms.properties file");
        }
    }

    @Override
    public String getLogin() {
        return login;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getAlphaName() {
        return alphaName;
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
