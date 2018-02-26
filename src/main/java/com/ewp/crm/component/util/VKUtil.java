package com.ewp.crm.component.util;

import com.ewp.crm.exceptions.parse.ParseClientException;
import com.ewp.crm.exceptions.util.VKAccessTokenException;
import com.ewp.crm.models.Client;
import com.sun.istack.internal.NotNull;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static String accessToken;

    private static Logger logger = LoggerFactory.getLogger(VKUtil.class);

    @PostConstruct
    private void init() {
        boolean configInitialized = checkConfig();
        if (!configInitialized) {
            logger.error("VK configs have not initialized. Check files of properties");
        } else {
            initAccessToken();
        }
    }

    private boolean checkConfig() {
        if (clientId == null || "".equals(clientId)) return false;
        if (clientSecret == null || "".equals(clientSecret)) return false;
        if (username == null || "".equals(username)) return false;
        if (password == null || "".equals(password)) return false;
        if (clubId == null || "".equals(clubId)) return false;
        return true;
    }

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
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(httpGet);
            String result = EntityUtils.toString(response.getEntity());
            try {
                JSONObject json = new JSONObject(result);
                accessToken = json.getString("access_token");
            } catch (JSONException e) {
                logger.error("Perhaps the VK username/password configs are incorrect. Can not get AccessToken");
            }
        } catch (IOException e) {
            logger.error("Failed to connect to VK server");
        }
    }

    public List<String> getNewMassages() throws VKAccessTokenException {
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
        if (accessToken == null) {
            throw new VKAccessTokenException("VK access token has not got");
        }
        try {
            HttpGet httpGetMessages = new HttpGet(uriGetMassages);
            HttpGet httpMarkMessages = new HttpGet(uriMarkAsRead);
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(httpGetMessages);
            String result = EntityUtils.toString(response.getEntity());
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
            logger.error("Can not read message from JSON");
        }
        catch (IOException e) {
            logger.error("Failed to connect to VK server");
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

