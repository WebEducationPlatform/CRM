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

    private String applicationToken;

    private static Logger logger = LoggerFactory.getLogger(VKConfigImpl.class);

    @Autowired
    public VKConfigImpl(Environment env) {
        clubId = env.getProperty("vk.club.id");
        version = env.getProperty("vk.version");
        communityToken = env.getProperty("vk.community.token");
        applicationToken = env.getProperty("vk.app.token");

        if (!configIsValid()) {
            logger.error("VK configs have not initialized. Check vk.properties file");
            System.exit(-1);
        }
    }

	private boolean configIsValid() {
		if (clubId == null || clubId.isEmpty()) return false;
		if (communityToken == null || communityToken.isEmpty()) return false;
		if (applicationToken == null || applicationToken.isEmpty()) return false;
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

    public String getApplicationToken() {
        return applicationToken;
    }
}
