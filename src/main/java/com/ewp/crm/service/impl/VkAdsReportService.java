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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.time.LocalDateTime;

@Component("VkAds")
public class VkAdsReportService implements AdReportService {
    private final VKConfig vkConfig;
    private final String vkApi;
    private final String version;
    private final String adsClientId;
    private final String accessToken;
    private final String vkAdsBudgetUri;

    @Autowired
    public VkAdsReportService(VKConfig vkConfig) {
        this.vkConfig = vkConfig;
        vkApi = vkConfig.getVkApiUrl();
        version = vkConfig.getVersion();
        adsClientId = vkConfig.getVkAdsClientId();
        accessToken = vkConfig.getVkAppAccessToken();
        vkAdsBudgetUri = vkAdsBudgetUri(); //строка запроса для получения баланса рекламногокабинета ВК
    }

    //формирование строки запроса для получения статистики рекламного кабинетв ВК
    private String vkAdsStatUri() {
        String dateTo = LocalDateTime.now().toLocalDate().toString(); //текущая дата
        String dateFrom = LocalDateTime.now().minusDays(1).toLocalDate().toString(); //вчерашняя дата - отняли один день
        StringBuilder stb = new StringBuilder(vkApi).append("ads.getStatistics")
                .append("?account_id=").append(adsClientId)
                .append("&ids_type=office")
                .append("&ids=").append(adsClientId)
                .append("&period=day")
                .append("&date_from=").append(dateFrom)
                .append("&date_to=").append(dateTo)
                .append("&v=").append(version)
                .append("&access_token=").append(accessToken);
        return stb.toString();
    }

    //формирование строки запроса для получения баланса рекламногокабинета ВК
    private String vkAdsBudgetUri() {
        StringBuilder stb = new StringBuilder(vkApi).append("ads.getBudget")
                .append("?account_id=").append(adsClientId)
                .append("&v=").append(version)
                .append("&access_token=").append(accessToken);
        return stb.toString();
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
        return new JSONObject(result);
    }

    //получение суммы потраченных денег в реламном кабинете из json
    private String spentFromJson(JSONObject jsonStat) throws JSONException {
        String spent = "0.00";
        JSONArray response = jsonStat.getJSONArray("response");
            for (int i = 0; i < response.length() ; i++) {
                JSONObject item = response.getJSONObject(i);
                if (item.has("stats")) {
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
        JSONObject jsonBalance = getAdsFromVkApi(vkAdsBudgetUri);
        return jsonBalance.getString("response");
    }

    //получение отчета по потраченным средствам из рекламного кабинета ВК
    public String getSpentMoney() throws JSONException, IOException {
        String vkAdsStatisticsUri = vkAdsStatUri();
        JSONObject jsonSpentMoney = getAdsFromVkApi(vkAdsStatisticsUri);
        return spentFromJson(jsonSpentMoney);
    }
}


