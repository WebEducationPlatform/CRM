package com.ewp.crm.controllers;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

@Controller
@RequestMapping("/slack")
public class SlackController {

    @GetMapping("{email}")
    public String slackInvite(@PathVariable String email) throws IOException {
        FileInputStream fis = new FileInputStream("slack.properties");
        Properties properties = new Properties();
        properties.load(fis);
        String token = properties.getProperty("slack.legacyToken");
        String url = "https://slack.com/api/users.admin.invite?" +
                "email=" + email +
                "&token=" + token;
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(new HttpGet(url))) {
            HttpEntity entity = response.getEntity();
            String json = EntityUtils.toString(entity);
            JSONObject result = new JSONObject(json);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "redirect:/client";
    }
}
