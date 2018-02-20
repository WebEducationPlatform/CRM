package com.ewp.crm.component;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


@Component
public class VKNotifier {

    @Value("${vk.app.clientId}")
    private String clientId;

    @Value("${vk.app.clientSecret}")
    private String clientSecret;

    @Value("${vk.profile.username}")
    private String username;

    @Value("${vk.profile.password}")
    private String password;

    @Value("${vk.club.id}")
    private String clubId;

    private String accessToken;

    @PostConstruct
    private void initAccessToken() {
        String uri =
                "https://oauth.vk.com/token" +
                "?grant_type=password" +
                "&client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&username=" + username +
                "&password=" + password;

        HttpGet httpGet = new HttpGet(uri);

        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpResponse response = httpClient.execute(httpGet);
            String result = EntityUtils.toString(response.getEntity());
            try {
                JSONObject json = new JSONObject(result);
               accessToken = json.getString("access_token");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getNewMassages() {
        String uri =
                "https://api.vk.com/method/"+
                "messages.getHistory"+
                "?user_id="+ clubId +
                "&rev=1" +
                "&version=5.73" +
                "&access_token=" + accessToken;

        HttpGet httpGet = new HttpGet(uri);
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpResponse response = httpClient.execute(httpGet);
            String result = EntityUtils.toString(response.getEntity());
            try {
                JSONObject json = new JSONObject(result);
                JSONArray jsonMessages = json.getJSONArray("response");
                List<String> resultList = new ArrayList<>();
                for (int i = 1; i < jsonMessages.length(); i++) {
                    JSONObject jsonMessage = jsonMessages.getJSONObject(i);
                    if ((clubId.equals(jsonMessage.getString("uid"))) && (jsonMessage.getInt("read_state") == 0)) {
                        resultList.add(jsonMessage.getString("body"));
                    }
                }
                return resultList;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

