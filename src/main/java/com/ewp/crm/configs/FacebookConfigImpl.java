package com.ewp.crm.configs;

import com.ewp.crm.configs.inteface.FacebookConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource("file:./facebook.properties")
public class FacebookConfigImpl implements FacebookConfig {

    private String version;
    private String schedulerCron;
    private String pageToken;
    private String pageId;

    private static Logger logger = LoggerFactory.getLogger(FacebookConfigImpl.class);

    @Autowired
    public FacebookConfigImpl(Environment env) {
        this.version = env.getProperty("fb.version");
        this.schedulerCron = env.getProperty("fb.scheduler.cron");
        this.pageToken = env.getProperty("fb.page.token");
        this.pageId = env.getProperty("fb.page.Id");
        if (!configIsValid()) {
            logger.error("Facebook configs have not initialized. Check fb.properties file");
            System.exit(-1);
        }
    }

	private boolean configIsValid() {
        // If we miss important props or this props is empty return false
        return !((version == null || version.isEmpty()) ||
                (schedulerCron == null || schedulerCron.isEmpty()) ||
                (pageToken == null || pageToken.isEmpty()) ||
                (pageId == null || pageId.isEmpty()));
	}

    public String getVersion() {
        return version;
    }
    
    public String getSchedulerCron() {
        return schedulerCron;
    }
    
    public String getPageToken() {
        return pageToken;
    }

    public String getPageId() {
        return  pageId;
    }
}
