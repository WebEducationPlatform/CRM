package com.ewp.crm.configs;

import com.ewp.crm.configs.inteface.YandexDirectConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = "file:./yandex-direct.properties", encoding = "windows-1251")
public class YandexDirectConfigImpl implements YandexDirectConfig {
    private static Logger logger = LoggerFactory.getLogger(YandexDirectConfigImpl.class);
    private String apiV4LiveUrl;
    private String apiV5Url;
    private String authorizationToken;
    private String acceptLanguage;
    private String clientLogin;

    @Autowired
    public YandexDirectConfigImpl(Environment env) {
        try {
            apiV4LiveUrl = env.getRequiredProperty("api.v4.live.url");
            apiV5Url = env.getRequiredProperty("api.v5.url");
            authorizationToken = env.getRequiredProperty("authorization.token");
            acceptLanguage= env.getRequiredProperty("accept.language");
            clientLogin= env.getRequiredProperty("client.login");
        } catch (Exception e) {
            logger.error("Yandex-direct configs haven't been initialized. Check yandex-direct.properties file", e);
        }
    }

    @Override
    public String getApiV4LiveUrl() {
        return apiV4LiveUrl;
    }

    @Override
    public String getApiV5Url() {
        return apiV5Url;
    }

    @Override
    public String getAuthorizationToken() {
        return authorizationToken;
    }

    @Override
    public String getAcceptLanguage() {
        return acceptLanguage;
    }

    @Override
    public String getClientLogin() {
        return clientLogin;
    }
}
