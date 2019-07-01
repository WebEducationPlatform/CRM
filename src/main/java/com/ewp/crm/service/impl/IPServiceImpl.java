package com.ewp.crm.service.impl;

import com.ewp.crm.configs.inteface.IPConfig;
import com.ewp.crm.service.interfaces.IPService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class IPServiceImpl implements IPService {

    private String voximplantApiKey;

    private String voximplantAccountId;

    private String voximplantRuleId;

    private String voximplantLoginForWebCall;

    private String voximplantPasswordForWebCall;

    private String voximplantCodeToSetRecord;

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(IPServiceImpl.class);


    @Autowired
    public IPServiceImpl(IPConfig ipConfig) {
        voximplantApiKey = ipConfig.getVoximplantApiKey();
        voximplantAccountId = ipConfig.getVoximplantAccountId();
        voximplantRuleId = ipConfig.getVoximplantRuleId();
        voximplantLoginForWebCall = ipConfig.getVoximplantLoginForWebCall();
        voximplantPasswordForWebCall = ipConfig.getVoximplantPasswordForWebCall();
        voximplantCodeToSetRecord = ipConfig.getVoximplantCodeToSetRecord();
    }

    @Override
    public void call(String from, String to, Long callId) {

        String callVoximplant = "https://api.voximplant.com/platform_api/StartScenarios/" +
                "?account_id=" +
                voximplantAccountId +
                "&api_key=" +
                voximplantApiKey +
                "&rule_id=" +
                voximplantRuleId +
                "&script_custom_data=" +
                from +
                "%3A" +
                to +
                "%3A" +
                callId;

        try {
            HttpGet callVox = new HttpGet(callVoximplant);
            HttpClient httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setCookieSpec(CookieSpecs.STANDARD).build())
                    .build();
            httpClient.execute(callVox);
        } catch (IOException e) {
            logger.error("Failed to connect to Voximplant server ", e);
        }
    }

    @Override
    public String getBalanceOfVoximplantAccountInfo() {
        String result = "not available";
        final String url = "https://api.voximplant.com/platform_api/GetAccountInfo" +
                "?account_id=" +
                voximplantAccountId +
                "&api_key=" +
                voximplantApiKey;
        final HttpPost request = new HttpPost(url);
        final HttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build())
                .build();
        try {
            final HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() != 200) {
                logger.error("[getBalanceOfVoximplantAccountInfo] Failed to get AccountInfo from Voximplant server: {}", response.getStatusLine().getStatusCode());
                return result;
            }
            final HttpEntity httpEntity = response.getEntity();
            if (httpEntity == null) {
                logger.error("[getBalanceOfVoximplantAccountInfo] Failed to get AccountInfo from Voximplant server: empty response body");
                return result;
            }
            final ObjectMapper mapper = new ObjectMapper();
            JsonNode node;
            try {
                node = mapper.readTree(httpEntity.getContent()).path("result");
            } catch (IOException e) {
                logger.error("[getBalanceOfVoximplantAccountInfo] Failed to parse AccountInfo from Voximplant server", e);
                return result;
            }
            final String balance = node.path("balance").asText();
            final String currency = node.path("currency").asText();
            return balance + " " + currency;
        } catch (IOException e) {
            logger.error("[getBalanceOfVoximplantAccountInfo] Failed to connect to Voximplant server", e);
        }
        return result;
    }

    @Override
    public Optional<String> getVoximplantLoginForWebCall() {
        return Optional.ofNullable(voximplantLoginForWebCall);
    }

    @Override
    public Optional<String> getVoximplantPasswordForWebCall() {
        return Optional.ofNullable(voximplantPasswordForWebCall);
    }

    @Override
    public Optional<String> getVoximplantUserLogin(String fullLogin) {
        return Optional.of(fullLogin.replaceAll("@.+", ""));
    }

    @Override
    public Optional<String> getVoximplantCodeToSetRecord() {
        return Optional.ofNullable(voximplantCodeToSetRecord);
    }
}


