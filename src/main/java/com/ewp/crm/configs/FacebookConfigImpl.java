package com.ewp.crm.configs;

import com.ewp.crm.configs.inteface.FacebookConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@PropertySource("file:./facebook.properties")
public class FacebookConfigImpl implements FacebookConfig {

    private String version;
    private String pageToken;
    private String pageId;

    private static Logger logger = LoggerFactory.getLogger(FacebookConfigImpl.class);

    @Autowired
    public FacebookConfigImpl(Environment env) {
        version = env.getProperty("fb.version");
        pageToken = env.getProperty("fb.page.token");
        pageId = env.getProperty("fb.page.Id");
        if (!configIsValid()) {
            logger.error("Facebook configs have not initialized. Check fb.properties file");
            System.exit(-1);
        }
    }

	private boolean configIsValid() {
		if (version== null || version.isEmpty()) return false;
		if (pageToken == null || pageToken.isEmpty()) return false;
		if (pageId == null || pageId.isEmpty()) return false;
		return true;
	}

    @PostConstruct
    private void checkInitializeProperties() {
        if (version.equals("*") || pageToken.equals("*")) {
            logger.error("Facebook configs have not initialized. Check facebook.properties file");
        }
    }

    public String getVersion() {
        return version;
    }

    public String getPageToken() {
        return pageToken;
    }

    public String getPageId() {
        return  pageId;
    }
}
