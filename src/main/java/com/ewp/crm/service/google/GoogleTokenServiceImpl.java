package com.ewp.crm.service.google;

import com.ewp.crm.configs.GoogleAPIConfigImpl;
import com.ewp.crm.models.GoogleToken;
import com.ewp.crm.repository.interfaces.GoogleTokenRepository;
import com.ewp.crm.service.interfaces.GoogleTokenService;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public class GoogleTokenServiceImpl implements GoogleTokenService {

    private static Logger logger = LoggerFactory.getLogger(GoogleTokenServiceImpl.class);

    private final GoogleTokenRepository tokenRepository;
    private final GoogleAPIConfigImpl googleAPIConfig;

    @Autowired
    public GoogleTokenServiceImpl(GoogleTokenRepository tokenRepository, GoogleAPIConfigImpl googleAPIConfig) {
        this.tokenRepository = tokenRepository;
        this.googleAPIConfig = googleAPIConfig;
    }

    @Override
    public Optional<GoogleToken> getToken(GoogleToken.TokenType tokenType) {
        return Optional.ofNullable(tokenRepository.getByTokenType(tokenType));
    }

    @Override
    public void createOrUpdate(GoogleToken accessToken, GoogleToken.TokenType tokenType) {
        GoogleToken token = accessToken;
        if (getToken(tokenType).isPresent()) {
            token = getToken(tokenType).get();
            token.setAccessToken(accessToken.getAccessToken());
            token.setRefreshToken(accessToken.getRefreshToken());
        }
        tokenRepository.saveAndFlush(token);
    }

    @Override
    public Optional<GoogleToken> getRefreshedToken(GoogleToken.TokenType tokenType) {
        String res = StringUtils.EMPTY;
        try {
            if (getToken(tokenType).isPresent()) {
                GoogleToken googleToken = getToken(tokenType).get();
                String uri = googleAPIConfig.getAccessTokenUri();

                HttpPost httpPostMessages = new HttpPost(uri);
                httpPostMessages.setHeader("Content-type", "application/json");
                String clientId = StringUtils.EMPTY;
                String clientSecret = StringUtils.EMPTY;
                switch (tokenType) {
                    case DRIVE:
                        clientId = googleAPIConfig.getDriveClientId();
                        clientSecret = googleAPIConfig.getDriveClientSecret();
                        break;
                    case CALENDAR:
                        clientId = googleAPIConfig.getCalendarClientId();
                        clientSecret = googleAPIConfig.getCalendarClientSecret();
                        break;
                }
                
                JsonObject root = new JsonObject();
                root.addProperty("refresh_token", googleToken.getRefreshToken());
                root.addProperty("client_id", clientId);
                root.addProperty("client_secret", clientSecret);
                root.addProperty("grant_type", "refresh_token");
                String refreshParam = root.toString();

                httpPostMessages.setEntity(new StringEntity(refreshParam));
                HttpClient httpClient = getHttpClient();
                HttpResponse response = httpClient.execute(httpPostMessages);
                res = EntityUtils.toString(response.getEntity());
                JSONObject json = new JSONObject(res);
                String accessToken = json.getString("access_token");
                googleToken.setAccessToken(accessToken);
                createOrUpdate(googleToken, tokenType);
                return Optional.of(googleToken);
            }
        } catch (IOException e) {
            logger.error("Error with upload json for refreshing token", e);
        } catch (JSONException e) {
            logger.error("Error with parsing json " + res, e);
        }
        return Optional.empty();
    }

    private HttpClient getHttpClient() {
        return HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build())
                .build();
    }
}
