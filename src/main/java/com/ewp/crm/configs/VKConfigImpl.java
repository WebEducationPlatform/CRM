package com.ewp.crm.configs;

import com.ewp.crm.configs.inteface.VKConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@PropertySource( value = "file:./vk.properties", encoding = "UTF-8")
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

    private String firstContactMessage;

    private String apiUrl;

    private String managerToken;

    private String vkReportChatId;

    private String vkAdsClientId;

    private String vkAppAccessToken;

    private String firstSkypeNotifyChatId;

    private String firstSkypeMessageTemplate;

    private String firstSkypeUpdateMessageTemplate;

    private String firstSkypeDeleteMessageTemplate;

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
            robotClientSecret = env.getRequiredProperty("vk.robot.app.clientSecret");
            firstContactMessage = env.getRequiredProperty("vk.robot.message.firstContact");
            apiUrl  = env.getRequiredProperty("vk.apiUrl");
            managerToken = env.getRequiredProperty("vk.manager.token");
            vkReportChatId = env.getRequiredProperty("vk.app.reports.service.chat.id");
            vkAdsClientId = env.getRequiredProperty("vk.ads.client.id");
            vkAppAccessToken = env.getRequiredProperty("vk.robot.app.accesstoken");
            firstSkypeNotifyChatId = env.getRequiredProperty("vk.firstSkypeNotify.chatId");
            firstSkypeMessageTemplate = env.getRequiredProperty("vk.firstSkypeNotify.template");
            firstSkypeUpdateMessageTemplate = env.getRequiredProperty("vk.firstSkypeNotify.updateTemplate");
            firstSkypeDeleteMessageTemplate = env.getRequiredProperty("vk.firstSkypeNotify.deleteTemplate");
        } catch (IllegalStateException e) {
            logger.error("VK configs have not initialized. Check vk.properties file", e);
            System.exit(1);
        }
    }

    @PostConstruct
    private void checkInitializeProperties() {
        if (clubId.equals("*") || communityToken.equals("*")) {
            logger.error("VK configs have not initialized. Check vk.properties file");
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

    public String getFirstContactMessage() {
        return firstContactMessage;
    }

    public String getVkApiUrl() {
        return apiUrl;
    }

    public String getManagerToken() {
        return managerToken;
    }

    public String getVkReportChatId() {
        return vkReportChatId;
    }

    public String getVkAdsClientId() {return vkAdsClientId; }

    public String getVkAppAccessToken() {
        return vkAppAccessToken;
    }

    @Override
    public String getFirstSkypeUpdateMessageTemplate() {
        return firstSkypeUpdateMessageTemplate;
    }

    @Override
    public String getFirstSkypeDeleteMessageTemplate() {
        return firstSkypeDeleteMessageTemplate;
    }

    @Override
    public String getFirstSkypeNotifyChatId() {
        return firstSkypeNotifyChatId;
    }

    @Override
    public String getFirstSkypeMessageTemplate() {
        return firstSkypeMessageTemplate;
    }

    public enum firstSkypeNotificationType {
        CREATE,
        UPDATE,
        DELETE
    }

}