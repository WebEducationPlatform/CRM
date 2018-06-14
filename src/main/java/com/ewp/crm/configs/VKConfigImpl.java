package com.ewp.crm.configs;

import com.ewp.crm.configs.inteface.VKConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:vk.properties")
public class VKConfigImpl implements VKConfig {

    private String clubId;

    private String version;

    private String communityToken;

    private String applicationId;

    private String display;

    private String redirectUri;

    private String scope;

    private static Logger logger = LoggerFactory.getLogger(VKConfigImpl.class);

    @Autowired
    public VKConfigImpl(Environment env) {
        clubId = env.getProperty("vk.club.id");
        version = env.getProperty("vk.version");
        communityToken = env.getProperty("vk.community.token");
        applicationId = env.getProperty("vk.app.id");
        display = env.getProperty("vk.app.display");
        redirectUri = env.getProperty("vk.app.redirect_uri");
        scope = env.getProperty("vk.app.scope");

        if (!configIsValid()) {
            logger.error("VK configs have not initialized. Check vk.properties file");
            System.exit(-1);
        }
    }

	private boolean configIsValid() {
		if (clubId == null || clubId.isEmpty()) return false;
		if (version== null || version.isEmpty()) return false;
		if (communityToken == null || communityToken.isEmpty()) return false;
		if (applicationId == null || applicationId.isEmpty()) return false;
		if (display == null || display.isEmpty()) return false;
		if (redirectUri == null || redirectUri.isEmpty()) return false;
		if (scope == null || scope.isEmpty()) return false;
		return true;
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
}
