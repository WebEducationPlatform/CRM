package com.ewp.crm.service.google;

import com.ewp.crm.configs.GoogleAPIConfigImpl;
import com.ewp.crm.models.GoogleToken;
import com.ewp.crm.repository.interfaces.GoogleTokenRepository;
import com.ewp.crm.service.interfaces.GoogleAuthorizationService;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;

@Service
public class GoogleAuthorizationServiceImpl implements GoogleAuthorizationService {

    private static Logger logger = LoggerFactory.getLogger(GoogleAuthorizationServiceImpl.class);
    private HttpTransport httpTransport;
    private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private final GoogleTokenRepository tokenRepository;

    private String clientId;
    private String clientSecret;
    private String redirectURI;
    private String scope;
    private GoogleClientSecrets clientSecrets;
    private GoogleAuthorizationCodeFlow flow;
    private Credential credential;

    @Autowired
    public GoogleAuthorizationServiceImpl(GoogleAPIConfigImpl config, GoogleTokenRepository tokenRepository) {
        this.clientId = config.getClientId();
        this.clientSecret = config.getClientSecret();
        this.redirectURI = config.getRedirectURI();
        this.scope = config.getScope();
        this.tokenRepository = tokenRepository;
    }

    @Override
    public String authorize() {
        AuthorizationCodeRequestUrl authorizationUrl;
        if (flow == null) {
            GoogleClientSecrets.Details web = new GoogleClientSecrets.Details();
            web.setClientId(clientId);
            web.setClientSecret(clientSecret);
            clientSecrets = new GoogleClientSecrets().setWeb(web);
            try {
                httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            } catch (GeneralSecurityException | IOException e) {
                logger.error("Error to send message ", e);
            }
            Collection<String> collectionScope = Arrays.asList(scope.split(","));
            flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets,
                    collectionScope).setAccessType("offline").build();
        }
        authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectURI);
        return authorizationUrl.build();
    }

    @Override
    public Credential tokenResponse(String code) {
        try {
            TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectURI).execute();
            tokenRepository.saveAndFlush(new GoogleToken(response.getAccessToken()));
            credential = flow.createAndStoreCredential(response, "userID");
            return credential;
        } catch (IOException e) {
            logger.warn("Exception while handling OAuth2 callback (" + e.getMessage() + ")."
                    + " Redirecting to google connection status page.");
            logger.error("Error to send message ", e);
        }
        return credential;
    }

    @Override
    public Credential getCredential() {
        return credential;
    }
}
