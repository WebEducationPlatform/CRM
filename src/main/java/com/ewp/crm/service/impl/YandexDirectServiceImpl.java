package com.ewp.crm.service.impl;

import com.ewp.crm.service.interfaces.YandexDirectService;
import com.squareup.okhttp.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
@PropertySource(value = "file:./yandex-direct.properties", encoding = "windows-1251")
public class YandexDirectServiceImpl implements YandexDirectService {

    private Environment env;

    private String urlAPIv4Live;
    private String tokenAPIv4Live;
    private String urlAPIv5;
    private String tokenAPIv5;
    private String acceptLanguage;
    private String clientLogin;

    private String balance;

    @Autowired
    public YandexDirectServiceImpl(Environment env) {
        urlAPIv4Live= env.getRequiredProperty("urlAPIv4Live");
        urlAPIv5= env.getRequiredProperty("urlAPIv5");
        tokenAPIv4Live = env.getRequiredProperty("AuthorizationAPIv4Live");
        tokenAPIv5 = env.getRequiredProperty("AuthorizationAPIv5");
        acceptLanguage= env.getRequiredProperty("Accept-Language");
        clientLogin= env.getRequiredProperty("Client-Login");
    }

    public String getYandexDirectBalance() throws JSONException, IOException {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        // Тело запроса к операции Get метода AccountManagement API v4.
        JSONObject bodyForHttpRequest = new JSONObject("{\n" +
            "\"method\": \"AccountManagement\",\n" +
                "   \"param\": {\n" +
                "      \"Action\": Get,\n" +
                "      \"SelectionCriteria\": {\n" +
                "         \"Logins\": [\n" + clientLogin + "]\n" +
                "      }\n" +
                "   }," +
                "\"locale\": \"" + acceptLanguage + "\"," +
                "\"token\": \"" + tokenAPIv4Live + "\"" +
            "}");
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

        if (jsonArray.length() != 0) {
            balance = jsonArray.getJSONObject(0).getString("Amount");
        }

        return "Текущий баланс составляет: " + balance;
    }

    // Этот метод настроен на взаимодействие с методами API v5.
    // Сейчас он не задействован, но может пригодиться в будущем для получения расширенной статистики.
    public String getYandexDirectStats() throws JSONException, IOException {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        // Тело запроса к методу get сервиса Campaigns API v5.
        JSONObject bodyForHttpRequest = new JSONObject("{\n" +
            "  \"method\": \"get\",\n" +
            "  \"params\": {\n" +
            "    \"SelectionCriteria\": {},\n" +
            "    \"FieldNames\": [\"Funds\", \"Currency\"]\n" +
            "  }\n" +
            "}");
        RequestBody stats = RequestBody.create(JSON, bodyForHttpRequest.toString());
        // Задание заголовков, URL, метода обращения. Используя сформированное выше тело, формируем запрос.
        Request requestStats = new Request.Builder()
                .addHeader("Authorization", tokenAPIv5)
                .addHeader("Accept-Language", acceptLanguage)
                .addHeader("Client-Login", clientLogin)
                .url(urlAPIv5)
                .post(stats).build();
        // Исполняем запрос
        OkHttpClient client = new OkHttpClient();
        Response responseStats = client.newCall(requestStats).execute();

        return responseStats.body().string();
    }
}