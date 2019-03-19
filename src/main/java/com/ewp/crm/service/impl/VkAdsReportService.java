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
import java.text.SimpleDateFormat;
import java.util.Date;

@Component("VkAds")
public class VkAdsReportService implements AdReportService {
    private static Logger logger = LoggerFactory.getLogger(VkAdsReportService.class);
    private final VKConfig vkConfig;

    private String vkAPI;
    private String version;
    private String adsClientId;
    private String access_token;

    private Date date = new Date();
    private Date dateBeforeOneDay = new Date(date.getTime() - 24 * 3600 * 1000l );
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String dateTo = simpleDateFormat.format(date);
    private String dateFrom = simpleDateFormat.format(dateBeforeOneDay);


    @Autowired
    public VkAdsReportService(VKConfig vkConfig) {
        this.vkConfig = vkConfig;
        try {
            vkAPI = vkConfig.getVkAPIUrl();
            version = vkConfig.getVersion();
            adsClientId = vkConfig.getVkAdsClientId();
            access_token = vkConfig.getVkAppAccessToken();
            if(vkAPI.isEmpty() || version.isEmpty() || adsClientId.isEmpty() || access_token.isEmpty()) {
                throw new NullPointerException();
            }
        } catch (Exception e) {
            logger.error("VKConfig haven't been initialized. Check vk.properties file parameters vk.apiUrl, vk.version, vk.ads.ClientId or vk.robot.app.accesstoken" );
        }
    }

    //формирование строки запроса для получения статистики рекламного кабинетв ВК
    public String vkAdsStatUri() {
        StringBuilder stb = new StringBuilder(vkAPI).append("ads.getStatistics")
                .append("?account_id=").append(adsClientId)
                .append("&ids_type=office")
                .append("&ids=").append(adsClientId)
                .append("&period=day")
                .append("&date_from=").append(dateFrom)
                .append("&date_to=").append(dateTo)
                .append("&version=").append(version)
                .append("&access_token=").append(access_token);
        return stb.toString();
    }

    //формирование строки запроса для получения баланса рекламногокабинета ВК
    public String vkAdsBudgetUri() {
        StringBuilder stb = new StringBuilder(vkAPI).append("ads.getBudget")
                .append("?account_id=").append(adsClientId)
                .append("&version=").append(version)
                .append("&access_token=").append(access_token);
        return stb.toString();
    }

    //выполнение запроса и получение json ответа
    public JSONObject getJsonByUri(String uri) {
        HttpGet httpGetStat = new HttpGet(uri);
        HttpClient httpClientStat = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build())
                .build();
        HttpResponse response = null;
        JSONObject json = null;
        try {
            response = httpClientStat.execute(httpGetStat);
            String result = EntityUtils.toString(response.getEntity());
            json = new JSONObject(result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  json;
    }

    //получение баланса рекламного кабинета вконтакте из json
    public String balanceFromJson(JSONObject jsonBalance) {
        String balance = "";
        try {
            balance = jsonBalance.getString("response");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return balance;
    }

    //получение суммы потраченных денег в реламном кабинете из json
    public String spentFromJson(JSONObject jsonStat) {
        String spent = "0.00";
        JSONArray responce = null;
        try {
            responce = jsonStat.getJSONArray("response");
            for (int i = 0; i < responce.length() ; i++) {
                JSONObject item = responce.getJSONObject(i);
                if(item.has("stats")) {
                    JSONArray stats = item.getJSONArray("stats");
                    for (int j = 0; j < stats.length() ; j++) {
                        JSONObject aim = stats.getJSONObject(j);
                        if (aim.has("spent")) {
                            spent = aim.getString("spent");
                        }
                    }
                }
            }    } catch (JSONException e) {
            e.printStackTrace();
        }
        return spent;
    }

    //получение баланса рекламного кабинета вк
    public String getBalance() throws JSONException {
        String balanceUri = vkAdsBudgetUri();
        JSONObject jsonBalance = getJsonByUri(balanceUri);
     String balance = balanceFromJson(jsonBalance);
        return  balance;
    }

    //получение отчета по потраченным средствам из рекламного кабинета ВК
    public String getSpentMoney() throws JSONException {
        String spentMoneyUri = vkAdsStatUri();
        JSONObject jsonSpentMoney = getJsonByUri(spentMoneyUri);
       String spentMoney = spentFromJson(jsonSpentMoney);
        return spentMoney;
    }
}


