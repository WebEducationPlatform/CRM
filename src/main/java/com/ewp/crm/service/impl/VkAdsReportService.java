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
import java.text.SimpleDateFormat;
import java.util.Date;

@Component("VkAds")
public class VkAdsReportService implements AdReportService {
    private final VKConfig vkConfig;

    private String vkAPI;
    private String redirectUri;
    private String version;
    private String adsClientId;
    private String appClientId;
    private String access_token;

 //   private String vkAppRedirect_uri = "https://oauth.vk.com/blank.html";
    Date date = new Date();
    Date dateBeforeOneDay = new Date(date.getTime() - 24 * 3600 * 1000l );
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String dateTo = simpleDateFormat.format(date);
    String dateFrom = simpleDateFormat.format(dateBeforeOneDay);


    @Autowired
    public VkAdsReportService(VKConfig vkConfig) {
        this.vkConfig = vkConfig;
        vkAPI = vkConfig.getVkAPIUrl();
        redirectUri = vkConfig.getRedirectUri();
        version = vkConfig.getVersion();
        adsClientId = vkConfig.getVkAdsClientId();
        appClientId = vkConfig.getRobotClientId();
        access_token = vkConfig.getVkAppAccessToken();
    }


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

    public String vkAdsBudgetUri() {
        StringBuilder stb = new StringBuilder(vkAPI).append("ads.getBudget")
                .append("?account_id=").append(adsClientId)
                .append("&version=").append(version)
                .append("&access_token=").append(access_token);
        return stb.toString();
    }

    public String vkAppGetTokenUri() {
        StringBuilder stb = new StringBuilder("https://oauth.vk.com/authorize")
                .append("?client_id=").append(appClientId)
                .append("&display=page&redirect_uri=").append(redirectUri)
                .append("&scope=ads,offline,groups").append("&response_type=token")
                .append("&v=5.92&state=");
        return stb.toString();
    }

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

    public String balanceFromJson(JSONObject jsonBalance) {
        String balance = "";
        try {
            balance = jsonBalance.getString("response");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return balance;
    }


    public String spentFromJson(JSONObject jsonStat) {
        String spent = "0.00";
        Long clicks = 0L;
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
        return spent.toString();
    }

    public String getBalance() throws JSONException {
        String balanceUri = vkAdsBudgetUri();
        JSONObject jsonBalance = getJsonByUri(balanceUri);
      //  jsonBalance = new JSONObject("{\"response\":\"15.00\"}");
        String balance = balanceFromJson(jsonBalance);
        return  balance;
    }

    public String getSpentMoney() throws JSONException {
        String spentMoneyUri = vkAdsStatUri();
        JSONObject jsonSpentMoney = getJsonByUri(spentMoneyUri);
      //  jsonSpentMoney = new JSONObject("{\"response\":[{\"id\":1605137078,\"type\":\"office\",\"stats\":[{\"spent\":\"6.00\", \"clicks\":\"16.00\"}]}]}");
        String spentMoney = spentFromJson(jsonSpentMoney);
        return spentMoney;
    }


    public static void main(String[] args) throws JSONException {
      /*  Environment env;
        VkAdsReportService vkAdsReport = new VkAdsReportService(new VKConfigImpl(env));

        String baltest = "{\"response\":\"15.00\"}";
        String bigJson = "{\"response\":[{\"id\":1605137078,\"type\":\"office\",\"stats\":[{\"spent\":\"6.00\", \"clicks\":\"16.00\"}]}]}";
        String spent = vkAdsReport.spentFromJson(new JSONObject(bigJson));
        System.out.println("spent = " + spent);
        System.out.println(vkAdsReport.getBalance());
        System.out.println(vkAdsReport.getSpentMoney()); */
    }


}


/*
String uriBudget = "https://api.vk.com/method/" + "ads.getBudget" +
                "?account_id=" + "1605137078" +
                "&version=" + "5.78" +
                "&access_token=" + "524fb23e95f0049733785f33b6f33b09d7cb397d40c03d2a08fadc84dcbab2834452908c75719a7a56a2f";


    String uriStat = "https://api.vk.com/method/" + "ads.getStatistics" +
            "?account_id=" + "1605137078" +
            "&ids_type=" + "office" +
            "&ids=" + "1605137078" +
            "&period=" + "day" +
            "&date_from=" + "2019-03-15" +
            "&date_to=" + "2019-03-15" +
            "&version=" + "5.78" +
            "&access_token=" + "524fb23e95f0049733785f33b6f33b09d7cb397d40c03d2a08fadc84dcbab2834452908c75719a7a56a2f";



		получение токена
		https://oauth.vk.com/authorize?client_id=6886597&display=page&redirect_uri=http://example.com/callback&scope=friends&response_type=token&v=5.92&state=
https://oauth.vk.com/authorize?client_id=6886597&display=page&redirect_uri=http://localhost:9999/rest/vkontakte/ads&scope=ads,offline,groups&response_type=token&v=5.92&state=
c71b889e95665bbfd4a8f1480129f459f9899a789ee661210e9b13a8faa3bbdba6efbbf5d7f724bfd290a
этот токен действует вечно

	как получить рекламного vk баланс

Получение статистики рекламного кабинет в вк.
Произвести настройки перед запуском:
добавить адрес своего рекламного кабинета в vk.properties
https://vk.com/ads -> vk.accountId=1605137078 указать свой кабинет
в настройках приложения в вк указать redirect uri
http://localhost:9999/rest/vkontakte/ads
зайти на http://localhost:9999/vk-ads
статистика выведется в консоль и в браузер

accessToken = eb1134346a576830cacde3c61ce2f8525bda3012c3893853d23ce06100d6def88c027025d969372790df2
524fb23e95f0049733785f33b6f33b09d7cb397d40c03d2a08fadc84dcbab2834452908c75719a7a56a2f
expires = 0
https://api.vk.com/method/ads.getStatistics?account_id=1605137078&ids_type=office&ids=1605137078&period=day&date_from=2019-03-15&date_to=2019-03-15&version=5.78&access_token=524fb23e95f0049733785f33b6f33b09d7cb397d40c03d2a08fadc84dcbab2834452908c75719a7a56a2f
https://api.vk.com/method/ads.getBudget?account_id=1605137078&version=5.78&access_token=524fb23e95f0049733785f33b6f33b09d7cb397d40c03d2a08fadc84dcbab2834452908c75719a7a56a2f


guano (481-557-9415)
Идентификатор разработчика
PQFUFHeJkJFU4BMG7YCeHg


testguano (831-922-3882)
clientguanotest
740-899-9441
clientguanotest
748-585-6521


jmentortoapi
Идентификатор клиента
789730766343-9i6ih36puboskgkiaha1plmffrrpgjj1.apps.googleusercontent.com
Секрет клиента
8qnoVDkrdiMWlf2LJZbZ9-Q1
api.adwords.refreshToken=1/AMDrQEAQOWbgU9xeZkxDVvjLqh8ErC7fBuuUdOuS5rM
api.adwords.clientCustomerId=740-899-9441

https://github.com/googleads/googleads-java-lib


ID приложения:	6886597
Защищённый ключ:
L0TevlxjUg8oq2cZi2ZC
Сервисный ключ доступа:
af5ff643af5ff643af5ff64329af36e286aaf5faf5ff643f3220abcf47e709942696936


кабинет Сони 	1602201055
кабинет мой 	1605137078

            */