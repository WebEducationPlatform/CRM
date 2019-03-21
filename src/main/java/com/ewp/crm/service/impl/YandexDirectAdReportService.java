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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("YandexDirect")
public class YandexDirectAdReportService implements AdReportService {
//    private static Logger logger = LoggerFactory.getLogger(YandexDirectAdReportService.class);
    private final YandexDirectConfig yandexDirectConfig;
    private static final String API_V5_AUTHORIZATION_TOKEN_PREFIX = "Bearer ";
    private static final String API_V5_REPORT_SERVICE_URL_SUFFIX = "reports";
    private String apiV4LiveUrl;
    private String apiV5Url;
    private String authorizationToken;
    private String acceptLanguage;
    private String clientLogin;

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
                    .append("\"token\": \"").append(authorizationToken).append("\"")
                .append( "}");
        JSONObject bodyForHttpRequest = new JSONObject(requestBodyBuilder.toString());
        RequestBody balanceBody = RequestBody.create(JSON, bodyForHttpRequest.toString());

        // По заданному URL, методом POST, используя сформированное выше тело, формируем запрос.
        Request requestBalance = new Request.Builder()
                .url(apiV4LiveUrl)
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
                .addHeader("Authorization", API_V5_AUTHORIZATION_TOKEN_PREFIX + authorizationToken)
                .addHeader("Accept-Language", acceptLanguage)
                .addHeader("Client-Login", clientLogin)
                .addHeader("processingMode", "online")
                .addHeader("returnMoneyInMicros", "false")
                .url(apiV5Url + API_V5_REPORT_SERVICE_URL_SUFFIX)
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
        Pattern money = Pattern.compile("\\d+\\.\\d+");
        Matcher matcher = money.matcher(rawHttpResponse);
        while (matcher.find()) {
            result += Float.parseFloat(matcher.group());
        }
        return Float.toString(result);
    }
}