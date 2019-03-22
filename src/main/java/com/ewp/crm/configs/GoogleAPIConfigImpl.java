package com.ewp.crm.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource("file:./google-api.properties")
public class GoogleAPIConfigImpl {

    private String clientId;
    private String clientSecret;
    private String redirectURI;
    private String scope;
    private static Logger logger = LoggerFactory.getLogger(GoogleAPIConfigImpl.class);

    @Autowired
    public GoogleAPIConfigImpl(Environment env) {
        clientId = env.getProperty("google.client.client-id");
        clientSecret = env.getProperty("google.client.client-secret");
        redirectURI = env.getProperty("google.client.redirectUri");
        scope = env.getProperty("google.client.scope");
        if (!configIsValid()) {
            logger.error("Google configs have not initialized. Check google-api.properties file");
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

    public String getScope() {
        return scope;
    }
}