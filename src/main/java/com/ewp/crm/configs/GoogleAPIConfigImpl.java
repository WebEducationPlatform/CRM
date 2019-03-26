package com.ewp.crm.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource("file:./google-api.properties")
public class GoogleAPIConfigImpl {

    private String clientId;
    private String clientSecret;
    private String redirectURI;
    private String scope;
    private String accessTokenUri;
    private String docsUri;
    private String driveUploadUri;
    private String driveUpdateUri;
    private String folderId;
    private static Logger logger = LoggerFactory.getLogger(GoogleAPIConfigImpl.class);

    @Autowired
    public GoogleAPIConfigImpl(Environment env) {
        clientId = env.getProperty("google.client.client-id");
        clientSecret = env.getProperty("google.client.client-secret");
        accessTokenUri = env.getProperty("google.client.access-token-uri");
        redirectURI = env.getProperty("google.client.redirectUri");
        scope = env.getProperty("google.client.scope");
        docsUri = env.getProperty("google.docs.uri");
        driveUploadUri = env.getProperty("google.drive.upload-uri");
        driveUpdateUri = env.getProperty("google.drive.update-uri");
        folderId = env.getProperty("google.drive.folder-id");
        if (!configIsValid()) {
            logger.error("Google configs have not initialized. Check google-api.properties file");
            System.exit(-1);
        }
    }

    private boolean configIsValid() {
        if (clientId == null || clientSecret.isEmpty()) return false;
        return true;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRedirectURI() {
        return redirectURI;
    }

    public String getScope() {
        return scope;
    }

    public String getDocsUri() {
        return docsUri;
    }

    public String getDriveUploadUri() {
        return driveUploadUri;
    }

    public String getDriveUpdateUri() {
        return driveUpdateUri;
    }

    public String getAccessTokenUri() {
        return accessTokenUri;
    }

    public String getFolderId() {
        return folderId;
    }
}
