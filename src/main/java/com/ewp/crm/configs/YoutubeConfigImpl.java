package com.ewp.crm.configs;

import com.ewp.crm.configs.inteface.YoutubeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource("file:./youtube.properties")
public class YoutubeConfigImpl implements YoutubeConfig {

    private String apiKey;
    private String channelId;
    private static Logger logger = LoggerFactory.getLogger(YoutubeConfigImpl.class);

    @Autowired
    public YoutubeConfigImpl(Environment env) {
        apiKey = env.getProperty("youtube.apikey");
        channelId = env.getProperty("youtube.channel.id");

        if (!configIsValid()) {
            logger.error("Youtube configs have not initialized. Check youtube.properties file");
            System.exit(-1);
        }
    }

    private boolean configIsValid() {
        if (apiKey == null || apiKey.isEmpty()) return false;
        if (channelId == null || channelId.isEmpty()) return false;
        return true;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getChannelId() {
        return channelId;
    }
}
