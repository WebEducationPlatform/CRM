package com.ewp.crm.configs;

import com.ewp.crm.configs.inteface.VKConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@PropertySource("classpath:vk.properties")
public class VKConfigImpl implements VKConfig {

    private String clientId;

    private String clientSecret;

    private String username;

    private String password;

    private String clubId;

    private String version;

    private String communityToken;

    private static Logger logger = LoggerFactory.getLogger(VKConfigImpl.class);

    @Autowired
    public VKConfigImpl(Environment env) {
        clientId = env.getProperty("vk.app.clientId");
        clientSecret = env.getProperty("vk.app.clientSecret");
        username = env.getProperty("vk.profile.username");
        password = env.getProperty("vk.profile.password");
        clubId = env.getProperty("vk.club.id");
        version = env.getProperty("vk.version");
        communityToken = env.getProperty("vk.community.token");

        if (!configIsValid()) {
            logger.error("VK configs have not initialized. Check vk.properties file");
            System.exit(-1);
        }
    }

    public boolean containsIllegals(String communityToken) {
        Pattern pattern = Pattern.compile("[!~#@*+%{}<>\\[\\]|\"\\_^]");
        Matcher matcher = pattern.matcher(communityToken);
        return matcher.find();
    }

    private boolean configIsValid() {
        if (clientId == null || "".equals(clientId)) return false;
        if (clientSecret == null || "".equals(clientSecret)) return false;
        if (username == null || "".equals(username)) return false;
        if (password == null || "".equals(password)) return false;
        if (clubId == null || "".equals(clubId)) return false;
        if (communityToken == null || "".equals(communityToken)) return false;
        if (!containsIllegals(communityToken)) return false;
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

    public String getVersion() {
        return version;
    }

    public String getCommunityToken() {
        return communityToken;
    }
}
