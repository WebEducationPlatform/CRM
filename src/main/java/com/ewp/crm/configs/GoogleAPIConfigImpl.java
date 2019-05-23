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

    private final String driveClientId;
    private final String driveClientSecret;
    private final String calendarClientId;
    private final String calendarClientSecret;
    private final String driveRedirectUri;
    private final String calendarRedirectUri;
    private final String driveScope;
    private final String calendarScope;
    private final String accessTokenUri;
    private final String docsUri;
    private final String driveUploadUri;
    private final String driveUpdateUri;
    private final String folderId;
    private final String calendarName;
    private final String eventName;
    private final String viewUri;
    private final String driveUploadUriOld;
    private final String deleteFile;
    private final static Logger logger = LoggerFactory.getLogger(GoogleAPIConfigImpl.class);

    @Autowired
    public GoogleAPIConfigImpl(Environment env) {
        driveClientId = env.getProperty("google.client.client-id");
        driveClientSecret = env.getProperty("google.client.client-secret");
        accessTokenUri = env.getProperty("google.client.access-token-uri");
        driveRedirectUri = env.getProperty("google.client.drive.redirectUri");
        driveScope = env.getProperty("google.client.scope");
        docsUri = env.getProperty("google.docs.uri");
        driveUploadUri = env.getProperty("google.drive.upload-uri");
        driveUpdateUri = env.getProperty("google.drive.update-uri");
        folderId = env.getProperty("google.drive.folder-id");
        calendarClientId = env.getProperty("google.client.calendar.client-id");
        calendarClientSecret = env.getProperty("google.client.calendar.client-secret");
        calendarScope = env.getProperty("google.client.calendar.scope");
        calendarRedirectUri = env.getProperty("google.client.calendar.redirectUri");
        calendarName = env.getProperty("google.client.calendar.name");
        eventName = env.getProperty("google.client.calendar.eventName");
        viewUri = env.getProperty("google.docs.view.url");
        driveUploadUriOld = env.getProperty("google.drive.upload-uri-old");
        deleteFile = env.getProperty("google.drive.delete");
        if (!configIsValid()) {
            logger.error("Google configs have not initialized. Check google-api.properties file");
            System.exit(-1);
        }
    }

    private boolean configIsValid() {
        if (driveClientId == null || driveClientSecret.isEmpty() || calendarClientId == null || calendarClientSecret == null) return false;
        return true;
    }

    public String getDeleteFile() {
        return deleteFile;
    }

    public String getDriveClientId() {
        return driveClientId;
    }

    public String getDriveClientSecret() {
        return driveClientSecret;
    }

    public String getDriveRedirectUri() {
        return driveRedirectUri;
    }

    public String getDriveScope() {
        return driveScope;
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

    public String getCalendarClientId() {
        return calendarClientId;
    }

    public String getCalendarClientSecret() {
        return calendarClientSecret;
    }

    public String getCalendarScope() {
        return calendarScope;
    }

    public String getCalendarRedirectUri() {
        return calendarRedirectUri;
    }

    public String getCalendarName() {
        return calendarName;
    }

    public String getEventName() {
        return eventName;
    }

    public String getViewUri() {
        return viewUri;
    }

    public String getDriveUploadUriOld() {
        return driveUploadUriOld;
    }
}
