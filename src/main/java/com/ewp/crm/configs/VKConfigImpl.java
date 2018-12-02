package com.ewp.crm.configs;

import com.ewp.crm.configs.inteface.VKConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource( value = "file:./vk.properties", encoding = "windows-1251")
public class VKConfigImpl implements VKConfig {

    private String clubId;

    private String version;

    private String communityToken;

    private String applicationId;

    private String display;

    private String redirectUri;

    private String scope;

    private String robotClientSecret;

    private String robotClientId;

    private String robotUsername;

    private String robotPassword;

    private String firstContactMessage;

    private String apiUrl;


    private static Logger logger = LoggerFactory.getLogger(VKConfigImpl.class);

    @Autowired
    public VKConfigImpl(Environment env) {
        try {
            clubId = env.getRequiredProperty("vk.club.id");
            version = env.getRequiredProperty("vk.version");
            communityToken = env.getRequiredProperty("vk.community.token");
            applicationId = env.getRequiredProperty("vk.app.id");
            display = env.getRequiredProperty("vk.app.display");
            redirectUri = env.getRequiredProperty("vk.app.redirect_uri");
            scope = env.getRequiredProperty("vk.app.scope");
            robotClientId = env.getRequiredProperty("vk.robot.app.clientId");
            robotUsername = env.getRequiredProperty("vk.robot.profile.username");
            robotPassword = env.getRequiredProperty("vk.robot.profile.password");
            robotClientSecret = env.getRequiredProperty("vk.robot.app.clientSecret");
            firstContactMessage = env.getProperty("vk.robot.message.firstContact");
            apiUrl  = env.getProperty("vk.apiUrl");



            if (clubId.isEmpty() || version.isEmpty() || communityToken.isEmpty() || applicationId.isEmpty() ||
                    display.isEmpty() || redirectUri.isEmpty() || scope.isEmpty()) {
                throw new NullPointerException();
            }
        } catch (IllegalStateException | NullPointerException e) {
            logger.error("VK configs have not initialized. Check vk.properties file");
            System.exit(-1);
        }
    }

    public String getClubIdWithMinus() {
        return "-" + clubId;
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

    public String getApplicationId() {
        return applicationId;
    }

    public String getDisplay() {
        return display;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getScope() {
        return scope;
    }

    public String getRobotClientSecret() {
        return robotClientSecret;
    }

    public String getRobotClientId() {
        return robotClientId;
    }

    public String getRobotUsername() {
        return robotUsername;
    }

    public String getRobotPassword() {
        return robotPassword;
    }

    public String getFirstContactMessage() {
        return firstContactMessage;
    }

    public String getVkAPIUrl() {
        return apiUrl;
    }

}
