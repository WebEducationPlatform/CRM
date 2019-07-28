package com.ewp.crm.configs;

import com.ewp.crm.configs.inteface.SlackConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@PropertySource(value = "file:./slack.properties", encoding = "UTF-8")
public class SlackConfigImpl implements SlackConfig {
    private String appFirstToken;
    private String legacyToken;
    private String generalChannelId;

    private static Logger logger = LoggerFactory.getLogger(SlackConfigImpl.class);

    @Autowired
    public SlackConfigImpl(Environment env) {
        try {
            appFirstToken = env.getRequiredProperty("slack.appToken1");
            legacyToken = env.getRequiredProperty("slack.legacyToken");
            generalChannelId = env.getRequiredProperty("slack.workspace.generalChannelId");
        } catch (IllegalStateException e) {
            logger.error("Slack configs have not initialized. Check slack.properties file", e);
            System.exit(1);
        }
    }

    @PostConstruct
    private void checkInitializeProperties() {
        if (appFirstToken.equals("*") || legacyToken.equals("*") || generalChannelId.equals("*")) {
            logger.error("Slack configs have not initialized. Check slack.properties file");
        }
    }

    @Override
    public String getAppFirstToken() {
        return appFirstToken;
    }

    @Override
    public String getLegacyToken() {
        return legacyToken;
    }

    @Override
    public String getGeneralChannelId() {
        return generalChannelId;
    }
}