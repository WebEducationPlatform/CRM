package com.ewp.crm.service.impl;

import com.ewp.crm.service.interfaces.AdReportService;
import com.squareup.okhttp.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("YandexDirect")
@PropertySource(value = "file:./yandex-direct.properties", encoding = "windows-1251")
public class YandexDirectAdReportService implements AdReportService {
    private static Logger logger = LoggerFactory.getLogger(YandexDirectAdReportService.class);

    private Environment env;

    private String urlAPIv4Live;
    private String tokenAPIv4Live;
    private String urlReportsAPIv5;
    private String tokenAPIv5;
    private String acceptLanguage;
    private String clientLogin;

    @Autowired
    public YandexDirectAdReportService(Environment env) {
        try {
            urlAPIv4Live= env.getRequiredProperty("urlAPIv4Live");
            urlReportsAPIv5= env.getRequiredProperty("urlReportsAPIv5");
            tokenAPIv4Live = env.getRequiredProperty("AuthorizationAPIv4Live");
            tokenAPIv5 = env.getRequiredProperty("AuthorizationAPIv5");
            acceptLanguage= env.getRequiredProperty("Accept-Language");
            clientLogin= env.getRequiredProperty("Client-Login");
            if (urlAPIv4Live.isEmpty() || urlReportsAPIv5.isEmpty() || tokenAPIv4Live.isEmpty() ||
                    tokenAPIv5.isEmpty() || acceptLanguage.isEmpty() || clientLogin.isEmpty()) {
                throw new NullPointerException();
            }
        } catch (Exception e) {
            logger.error("yandex-direct configs haven't been initialized. Check yandex-direct.properties file");
        }
    }

    // Получение баланса обращением к API v4 Live
    public String getBalance() throws JSONException, IOException {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        // Тело запроса к операции Get метода AccountManagement API v4.
        StringBuilder requestBodyBuilder = new StringBuilder()
                .append("{\n")
                    .append("\"method\": \"AccountManagement\",\n")
                    .append( "   \"param\": {\n")
                    .append("      \"Action\": Get,\n")
                    .append("      \"SelectionCriteria\": {\n")
                    .append( "         \"Logins\": [\n").append(clientLogin).append("]\n")
                    .append("      }\n")
                    .append("   },")
                    .append( "\"locale\": \"").append(acceptLanguage).append("\",")
                    .append("\"token\": \"").append(tokenAPIv4Live).append("\"")
                .append( "}");
        JSONObject bodyForHttpRequest = new JSONObject(requestBodyBuilder.toString());
        RequestBody balanceBody = RequestBody.create(JSON, bodyForHttpRequest.toString());

        // По заданному URL, методом POST, используя сформированное выше тело, формируем запрос.
        Request requestBalance = new Request.Builder()
                .url(urlAPIv4Live)
                .post(balanceBody).build();

        // Исполняем запрос
        OkHttpClient client = new OkHttpClient();
        Response responseBalance = client.newCall(requestBalance).execute();

        // Обрабатываем ответ
        JSONObject jsonObject = new JSONObject(responseBalance.body().string());
        JSONObject responseData = jsonObject.getJSONObject("data");
        JSONArray jsonArray = responseData.getJSONArray("Accounts");

        return jsonArray.getJSONObject(0).getString("Amount");
    }

    // Получение отчёта обращением к API v5, сервису Reports, и расчёт потраченных средств.
    public String getSpentMoney() throws JSONException, IOException {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        // Тело запроса к методу get сервиса Campaigns API v5.
        StringBuilder requestBodyBuilder = new StringBuilder()
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
                .append("}");
        JSONObject bodyForHttpRequest = new JSONObject(requestBodyBuilder.toString());
        RequestBody report = RequestBody.create(JSON, bodyForHttpRequest.toString());

        // Задание заголовков, URL, метода обращения. Используя сформированное выше тело, формируем запрос.
        Request requestStats = new Request.Builder()
                .addHeader("Authorization", tokenAPIv5)
                .addHeader("Accept-Language", acceptLanguage)
                .addHeader("Client-Login", clientLogin)
                .addHeader("processingMode", "online")
                .addHeader("returnMoneyInMicros", "false")
                .url(urlReportsAPIv5)
                .post(report).build();
        // Исполняем запрос
        OkHttpClient client = new OkHttpClient();
        Response responseReport = client.newCall(requestStats).execute();

        // Обрабатываем и возвращаем ответ
        String responseAsString = responseReport.body().string();
        return summaryOfSpentMoney(responseAsString);
    }

    // Парсинг и суммирование потраченных денег по всем кампаниям
    private String summaryOfSpentMoney(String rawHttpResponse) {
        float result = 0.00f;
        Pattern p = Pattern.compile("\\d+\\.\\d+");
        Matcher m = p.matcher(rawHttpResponse);
        while (m.find()) {
            result += Float.parseFloat(m.group());
        }
        return Float.toString(result);
    }
}