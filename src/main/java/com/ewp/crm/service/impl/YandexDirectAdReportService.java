package com.ewp.crm.service.impl;

import com.ewp.crm.configs.inteface.YandexDirectConfig;
import com.ewp.crm.service.interfaces.AdReportService;
import com.squareup.okhttp.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("YandexDirect")
public class YandexDirectAdReportService implements AdReportService {

    private final YandexDirectConfig yandexDirectConfig;
    private static final String API_V5_AUTHORIZATION_TOKEN_PREFIX = "Bearer ";
    private static final String API_V5_REPORT_SERVICE_URL_SUFFIX = "reports";
    private final String apiV4LiveUrl;
    private final String apiV5Url;
    private final String authorizationToken;
    private final String acceptLanguage;
    private final String clientLogin;

    @Autowired
    public YandexDirectAdReportService(YandexDirectConfig yandexDirectConfig) {
        this.yandexDirectConfig = yandexDirectConfig;
        apiV4LiveUrl = yandexDirectConfig.getApiV4LiveUrl();
        apiV5Url = yandexDirectConfig.getApiV5Url();
        authorizationToken = yandexDirectConfig.getAuthorizationToken();
        acceptLanguage = yandexDirectConfig.getAcceptLanguage();
        clientLogin = yandexDirectConfig.getClientLogin();
    }

    // Получение баланса обращением к API v4 Live
    public String getBalance() throws JSONException, IOException {
        MediaType typeJson = MediaType.parse("application/json; charset=utf-8");

        // Тело запроса к операции Get метода AccountManagement API v4.
        RequestBody bodyForBalance = RequestBody.create(typeJson, new JSONObject(new StringBuilder()
                .append("{\n")
                .append("\"method\": \"AccountManagement\",\n")
                .append( "   \"param\": {\n")
                .append("      \"Action\": Get,\n")
                .append("      \"SelectionCriteria\": {\n")
                .append( "         \"Logins\": [\n").append(clientLogin).append("]\n")
                .append("      }\n")
                .append("   },")
                .append( "\"locale\": \"").append(acceptLanguage).append("\",")
                .append("\"token\": \"").append(authorizationToken).append("\"")
                .append( "}")
                .toString())
                .toString());

        // По заданному URL, методом POST, используя сформированное выше тело, формируем запрос.
        Request requestForBalance = new Request.Builder()
                .url(apiV4LiveUrl)
                .post(bodyForBalance).build();

        // Исполняем запрос
        OkHttpClient requestClient = getCustomizedClient();
        Response response = requestClient.newCall(requestForBalance).execute();

        // Обрабатываем ответ
        JSONArray responseData = new JSONObject(response.body().string())
                .getJSONObject("data")
                .getJSONArray("Accounts");

        return responseData.getJSONObject(0).getString("Amount");
    }

    // Получение отчёта обращением к API v5, сервису Reports, и расчёт потраченных средств.
    public String getSpentMoney() throws JSONException, IOException {
        MediaType typeJson = MediaType.parse("application/json; charset=utf-8");

        // Тело запроса к методу get сервиса Campaigns API v5.
        RequestBody bodyForReport = RequestBody.create(typeJson, new JSONObject(new StringBuilder()
                .append("{\n")
                .append(  "\"params\": {\n")
                .append("        \"SelectionCriteria\": {},\n")
                .append("        \"FieldNames\": [\n")
                .append("            \"Cost\"\n")
                .append("        ],\n")
                .append("        \"ReportName\": \"YaD Daily Spent Money Stats\",\n")
                .append("        \"ReportType\": \"CAMPAIGN_PERFORMANCE_REPORT\",\n")
                .append("        \"DateRangeType\": \"YESTERDAY\",\n")
                .append("        \"Format\": \"TSV\",\n")
                .append("        \"IncludeVAT\": \"YES\",\n")
                .append("        \"IncludeDiscount\": \"YES\"\n")
                .append("    }\n")
                .append("}")
                .toString())
                .toString());

        // Задание заголовков, URL, метода обращения. Используя сформированное выше тело, формируем запрос.
        Request requestForReport = new Request.Builder()
                .addHeader("Authorization", API_V5_AUTHORIZATION_TOKEN_PREFIX + authorizationToken)
                .addHeader("Accept-Language", acceptLanguage)
                .addHeader("Client-Login", clientLogin)
                .addHeader("processingMode", "online")
                .addHeader("returnMoneyInMicros", "false")
                .url(apiV5Url + API_V5_REPORT_SERVICE_URL_SUFFIX)
                .post(bodyForReport)
                .build();
        // Исполняем запрос
        OkHttpClient requestClient = getCustomizedClient();
        Response response = requestClient.newCall(requestForReport).execute();

        // Обрабатываем и возвращаем ответ
        String reportAsString = response.body().string();
        return summaryOfSpentMoney(reportAsString);
    }

    // Парсинг и суммирование потраченных денег по всем кампаниям
    private String summaryOfSpentMoney(String rawResponse) {
        float result = 0.00f;
        Pattern money = Pattern.compile("\\d+\\.\\d+");
        Matcher matcher = money.matcher(rawResponse);
        while (matcher.find()) {
            result += Float.parseFloat(matcher.group());
        }
        return Float.toString(result);
    }

    private OkHttpClient getCustomizedClient() {
        OkHttpClient requestClient = new OkHttpClient();
        requestClient.setConnectTimeout(30, TimeUnit.SECONDS);
        requestClient.setReadTimeout(30, TimeUnit.SECONDS);
        requestClient.setWriteTimeout(30, TimeUnit.SECONDS);
        return requestClient;
    }
}