package com.ewp.crm.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:vk.properties")
public class VKConfig {

    private String clientId;

    private String clientSecret;

    private String username;

    private String password;

    private String clubId;

    private static Logger logger = LoggerFactory.getLogger(VKConfig.class);

    @Autowired
    public VKConfig(Environment env, ConfigurableApplicationContext context) {
        clientId = env.getProperty("vk.app.clientId");
        clientSecret = env.getProperty("vk.app.clientSecret");
        username  = env.getProperty("vk.profile.username");
        password = env.getProperty("vk.profile.password");
        clubId  = env.getProperty("vk.club.id");

        if (!configValided()) {
            logger.error("VK configs have not initialized. Check files of properties");
            System.exit(-1);
        }
    }

    private boolean configValided() {
        if (clientId == null || "".equals(clientId)) return false;
        if (clientSecret == null || "".equals(clientSecret)) return false;
        if (username == null || "".equals(username)) return false;
        if (password == null || "".equals(password)) return false;
        if (clubId == null || "".equals(clubId)) return false;
        return true;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getClubId() {
        return clubId;
    }
}
