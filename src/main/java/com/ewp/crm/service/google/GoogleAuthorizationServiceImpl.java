package com.ewp.crm.service.google;

import com.ewp.crm.configs.GoogleAPIConfigImpl;
import com.ewp.crm.models.GoogleToken;
import com.ewp.crm.service.interfaces.GoogleAuthorizationService;
import com.ewp.crm.service.interfaces.GoogleTokenService;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class GoogleAuthorizationServiceImpl implements GoogleAuthorizationService {

    private static Logger logger = LoggerFactory.getLogger(GoogleAuthorizationServiceImpl.class);
    private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private final GoogleTokenService googleTokenService;

    private final String driveClientId;
    private final String driveClientSecret;
    private final String calendarClientId;
    private final String calendarClientSecret;
    private final String driveRedirectUri;
    private final String calendarRedirectUri;
    private final String driveScope;
    private final String calendarScope;
    private GoogleAuthorizationCodeFlow flow;
    private Credential driveCredential;
    private Credential calendarCredential;
    private HttpTransport httpTransport;

    @Autowired
    public GoogleAuthorizationServiceImpl(GoogleAPIConfigImpl config, GoogleTokenService googleTokenService) {
        this.driveClientId = config.getDriveClientId();
        this.driveClientSecret = config.getDriveClientSecret();
        this.driveRedirectUri = config.getDriveRedirectUri();
        this.driveScope = config.getDriveScope();
        this.googleTokenService = googleTokenService;
        this.calendarClientId = config.getCalendarClientId();
        this.calendarClientSecret = config.getCalendarClientSecret();
        this.calendarScope = config.getCalendarScope();
        this.calendarRedirectUri = config.getCalendarRedirectUri();
    }

    @Override
    public String authorize(GoogleTokenService.TokenType tokenType) {
        String clientId = StringUtils.EMPTY;
        String clientSecret = StringUtils.EMPTY;
        List<String> scopes = new ArrayList<>();
        String redirectUri = StringUtils.EMPTY;
        switch (tokenType) {
            case DRIVE:
                clientId = driveClientId;
                clientSecret = driveClientSecret;
                scopes = Arrays.asList(driveScope.split(","));
                redirectUri = driveRedirectUri;
                break;
            case CALENDAR:
                clientId = calendarClientId;
                clientSecret = calendarClientSecret;
                scopes = Arrays.asList(calendarScope.split(","));
                redirectUri = calendarRedirectUri;
                break;
        }
        AuthorizationCodeRequestUrl authorizationUrl;
        if (flow == null) {
            GoogleClientSecrets.Details web = new GoogleClientSecrets.Details();
            web.setClientId(clientId);
            web.setClientSecret(clientSecret);
            GoogleClientSecrets clientSecrets = new GoogleClientSecrets().setWeb(web);
            try {
                httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            } catch (GeneralSecurityException | IOException e) {
                logger.error("Error to send message ", e);
            }
            flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets,
                    scopes).setAccessType("offline").setApprovalPrompt("force").build();
        }
        authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectUri);
        return authorizationUrl.build();
    }

    @Override
    public Credential tokenResponse(String code, GoogleTokenService.TokenType tokenType) {
        TokenResponse response;
        try {
            switch (tokenType) {
                case DRIVE:
                    response = flow.newTokenRequest(code).setRedirectUri(driveRedirectUri).execute();
                    driveCredential = flow.createAndStoreCredential(response, "userID");
                    googleTokenService.createOrUpdate(new GoogleToken(response.getAccessToken(), response.getRefreshToken()), tokenType);
                    return driveCredential;
                case CALENDAR:
                    response = flow.newTokenRequest(code).setRedirectUri(calendarRedirectUri).execute();
                    calendarCredential = flow.createAndStoreCredential(response, "userID");
                    googleTokenService.createOrUpdate(new GoogleToken(response.getAccessToken(), response.getRefreshToken()), tokenType);
                    return calendarCredential;
            }
        } catch (IOException e) {
            logger.warn("Exception while handling OAuth2 callback (" + e.getMessage() + ")."
                    + " Redirecting to google connection status page.");
            logger.error("Error to send message ", e);
        }
        return driveCredential;
    }

    @Override
    public Credential getCredential(GoogleTokenService.TokenType tokenType) {
        switch (tokenType) {
            case CALENDAR:
                return calendarCredential;
            default:
                return driveCredential;
        }
    }
}
