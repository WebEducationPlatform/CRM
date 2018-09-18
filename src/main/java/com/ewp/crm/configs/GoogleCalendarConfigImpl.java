package com.ewp.crm.configs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    private final static Log logger = LogFactory.getLog(GoogleCalendarConfigImpl.class);

    @Autowired
    public GoogleCalendarConfigImpl(Environment env) {
        clientId = env.getProperty("google.client.client-id");
        clientSecret = env.getProperty("google.client.client-secret");
        redirectURI = env.getProperty("google.client.redirectUri");

        if (!configIsValid()) {
            logger.error("Youtube configs have not initialized. Check youtube.properties file");
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
