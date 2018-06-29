package com.ewp.crm.configs;

import com.ewp.crm.configs.inteface.FacebookConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:facebook.properties")
public class FacebookConfigImpl implements FacebookConfig {

    private String version;
    private String pageToken;
    private String pageId;

//    private String applicationId;
//    private String redirectUri;
//    private String scope;

    private static Logger logger = LoggerFactory.getLogger(FacebookConfigImpl.class);

    @Autowired
    public FacebookConfigImpl(Environment env) {
        version = env.getProperty("fb.version");
        pageToken = env.getProperty("fb.page.token");
        pageId = env.getProperty("fb.page.Id");
//        applicationId = env.getProperty("fb.app.id");
//        redirectUri = env.getProperty("fb.app.redirect_uri");
//        scope = env.getProperty("vk.app.scope");
        if (!configIsValid()) {
            logger.error("Facebook configs have not initialized. Check fb.properties file");
            System.exit(-1);
        }
    }

	private boolean configIsValid() {
		if (version== null || version.isEmpty()) return false;
		if (pageToken == null || pageToken.isEmpty()) return false;
//		if (applicationId == null || applicationId.isEmpty()) return false;
//		if (redirectUri == null || redirectUri.isEmpty()) return false;
//		if (scope == null || scope.isEmpty()) return false;
		return true;
	}


    public String getVersion() {
        return version;
    }

    public String getPageToken() {
        return pageToken;
    }

    @Override
    public String getPageId() {
        return  pageId;
    }

//    public String getApplicationId() {
//        return applicationId;
//    }


//    public String getRedirectUri() {
//        return redirectUri;
//    }

//    public String getScope() {
//        return scope;
//    }
}
