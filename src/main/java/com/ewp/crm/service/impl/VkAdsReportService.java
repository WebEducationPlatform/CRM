package com.ewp.crm.service.impl;


import com.ewp.crm.configs.inteface.VKConfig;
import com.ewp.crm.service.interfaces.AdReportService;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component("VkAds")
public class VkAdsReportService implements AdReportService {
    private static Logger logger = LoggerFactory.getLogger(VkAdsReportService.class);
    private final VKConfig vkConfig;

    private String vkApi;
    private String version;
    private String adsClientId;
    private String accessToken;
    private String dateTo;
    private String dateFrom;
    private StringBuilder vkAdsStatUri;
    private StringBuilder vkAdsBudgetUri;

    @Autowired
    public VkAdsReportService(VKConfig vkConfig) {
        this.vkConfig = vkConfig;
        vkApi = vkConfig.getVkApiUrl();
        version = vkConfig.getVersion();
        adsClientId = vkConfig.getVkAdsClientId();
        accessToken = vkConfig.getVkAppAccessToken();
        dateTo = LocalDateTime.now().toLocalDate().toString()+"her"; //текущая дата
        dateFrom = LocalDateTime.now().minusDays(1).toLocalDate().toString(); //вчерашняя дата - отняли один день
        //строка запроса для получения статистики рекламного кабинетв ВК
        vkAdsStatUri = new StringBuilder(vkApi).append("ads.getStatistics")
                .append("?account_id=").append(adsClientId)
                .append("&ids_type=office")
                .append("&ids=").append(adsClientId)
                .append("&period=day")
                .append("&date_from=").append(dateFrom)
                .append("&date_to=").append(dateTo)
                .append("&version=").append(version)
                .append("&access_token=").append(accessToken);
        //строка запроса для получения баланса рекламногокабинета ВК
        vkAdsBudgetUri = new StringBuilder(vkApi).append("ads.getBudget")
                .append("?account_id=").append(adsClientId)
                .append("&version=").append(version)
                .append("&access_token=").append(accessToken);

    }

    //выполнение запроса и получение json ответа
    private JSONObject getAdsFromVkApi(String uri) throws JSONException, IOException {
        HttpGet httpGetStat = new HttpGet(uri);
        HttpClient httpClientStat = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build())
                .build();
            HttpResponse response = httpClientStat.execute(httpGetStat);
            String result = EntityUtils.toString(response.getEntity());
        JSONObject  json = new JSONObject(result);
        return  json;
    }

    //получение суммы потраченных денег в реламном кабинете из json
    private String spentFromJson(JSONObject jsonStat) throws JSONException {
        String spent = "0.00";
        JSONArray response = jsonStat.getJSONArray("response");
            for (int i = 0; i < response.length() ; i++) {
                JSONObject item = response.getJSONObject(i);
                if(item.has("stats")) {
                    JSONArray stats = item.getJSONArray("stats");
                    for (int j = 0; j < stats.length() ; j++) {
                        JSONObject aim = stats.getJSONObject(j);
                        if (aim.has("spent")) {
                            spent = aim.getString("spent");
                        }
                    }
                }
            }
        return spent;
    }

    //получение баланса рекламного кабинета вк
    public String getBalance() throws JSONException, IOException {
        JSONObject jsonBalance = getAdsFromVkApi(vkAdsBudgetUri.toString());
        String balance = jsonBalance.getString("response");
        return  balance;
    }

    //получение отчета по потраченным средствам из рекламного кабинета ВК
    public String getSpentMoney() throws JSONException, IOException {
        JSONObject jsonSpentMoney = getAdsFromVkApi(vkAdsStatUri.toString());
        String spentMoney = spentFromJson(jsonSpentMoney);
        return spentMoney;
    }
}


