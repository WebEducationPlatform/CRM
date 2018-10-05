package com.ewp.crm.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource("file:./google-calendar.properties")
public class GoogleCalendarConfigImpl {

    private String clientId;
    private String clientSecret;
    private String redirectURI;
    private static Logger logger = LoggerFactory.getLogger(ImageConfig.class);

    @Autowired
    public GoogleCalendarConfigImpl(Environment env) {
        clientId = env.getProperty("google.client.client-id");
        clientSecret = env.getProperty("google.client.client-secret");
        redirectURI = env.getProperty("google.client.redirectUri");

        if (!configIsValid()) {
            logger.error("Google configs have not initialized. Check google-calendar.properties file");
            System.exit(-1);
        }
    }

    private boolean configIsValid() {
        if (clientId == null || clientSecret.isEmpty()) return false;
        return true;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRedirectURI() {
        return redirectURI;
    }

}
