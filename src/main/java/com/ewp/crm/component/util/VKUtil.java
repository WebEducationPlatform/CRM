package com.ewp.crm.component.util;

import com.ewp.crm.exceptions.parse.ParseClientException;
import com.ewp.crm.models.Client;
import com.sun.istack.internal.NotNull;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AutoRetryHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


@Component
public class VKUtil {

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

    private final static HttpClient httpClient = new DefaultHttpClient();

    private static String accessToken;

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
        String uriGetMassages =
                "https://api.vk.com/method/"+
                "messages.getHistory"+
                "?user_id="+ clubId +
                "&rev=1" +
                "&version=5.73" +
                "&access_token=" + accessToken;

        String uriMarkAsRead =
                "https://api.vk.com/method/"+
                        "messages.markAsRead"+
                        "?peer_id="+ clubId +
                        "&version=5.73" +
                        "&access_token=" + accessToken;

        HttpGet httpGetMessages = new HttpGet(uriGetMassages);
        HttpGet httpMarkMessages = new HttpGet(uriMarkAsRead);
        try {
            HttpResponse response = httpClient.execute(httpGetMessages);
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
                httpClient.execute(httpMarkMessages);
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

    public Client parseClientFromMessage(@NotNull String message) throws ParseClientException {
        if (!message.startsWith("Новая заявка")) {
            throw new ParseClientException("Invalid message format");
        }

        String requestInfo = message.substring(0, message.indexOf("Q:"));

        message = message.replaceFirst(requestInfo, "");
        message = message.replace("<br>", "");
        message = message.replace(" ", "");

        String name;
        String lastName;
        String phoneNumber;
        String email;
        byte age;
        Client.Sex sex;

        try {
            String subMessage = message.substring(message.indexOf("Q:Имя:A:") + 8);
            name = subMessage.substring(0, subMessage.indexOf("Q:Фамилия:"));
            subMessage = message.substring(message.indexOf("Q:Фамилия:A:") + 12);
            lastName = subMessage.substring(0, subMessage.indexOf("Q:Возраст:"));
            subMessage = message.substring(message.indexOf("Q:Возраст:A:") + 12);
            age = Byte.parseByte(subMessage.substring(0, subMessage.indexOf("Q:Телефон:")));
            subMessage = message.substring(message.indexOf("Q:Телефон:A:") + 12);
            phoneNumber = subMessage.substring(0, subMessage.indexOf("Q:Электроннаяпочта:"));
            subMessage = message.substring(message.indexOf("Q:Электроннаяпочта:A:") + 21);
            email = subMessage.substring(0, subMessage.indexOf("Q:Вашпол:"));
            subMessage = message.substring(message.indexOf("Q:Вашпол:A:") + 11);
            String strSex = subMessage.toLowerCase();
            switch (strSex) {
                case "мужской":
                    sex = Client.Sex.MALE;
                    break;
                case "женский":
                    sex = Client.Sex.FEMALE;
                    break;
                default: throw  new ParseClientException("Couldn't parse sex");
            }
        } catch (Exception e) {
            throw new ParseClientException("Couldn't parse vk message", e);
        }

        Client resultClient = new Client(name, lastName, phoneNumber, email, age, sex);
        return resultClient;
    }
}

