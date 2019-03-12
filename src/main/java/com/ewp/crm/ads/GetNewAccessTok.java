package com.ewp.crm.ads;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.Map;

public class GetNewAccessTok {

    RestTemplate restTemplate = new RestTemplate();

    public void GetRefreshedTok(String client_id, String client_secret, String refresh_token) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap();
        String requestUrl = "https://www.googleapis.com/oauth2/v4/token";

        params.add("client_id", client_id);
        params.add("client_secret", client_secret);
        params.add("refresh_token", refresh_token);
        params.add("grant_type", "refresh_token");

        String result = restTemplate.postForObject(requestUrl, params, String.class);
        Gson gson = new Gson();

        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> read = gson.fromJson(result, type);
        System.out.println("URAI");
    }

    public static void main(String[] args) {
        GetNewAccessTok getNewAccessTok = new GetNewAccessTok();
        getNewAccessTok.GetRefreshedTok("789730766343-9i6ih36puboskgkiaha1plmffrrpgjj1.apps.googleusercontent.com", "8qnoVDkrdiMWlf2LJZbZ9-Q1", "1/AMDrQEAQOWbgU9xeZkxDVvjLqh8ErC7fBuuUdOuS5rM");
    }
}
