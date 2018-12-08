package com.ewp.crm.controllers;

import com.ewp.crm.service.slack.SlackServiceImpl;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/slack")
public class SlackController {

    private String INVITE_TOKEN;
    private static Logger logger = LoggerFactory.getLogger(SlackServiceImpl.class);

    @Autowired
    public SlackController(Environment environment) {
        try {
            this.INVITE_TOKEN = environment.getRequiredProperty("slack.inviteToken");
            if (INVITE_TOKEN.isEmpty()) {
                throw new NullPointerException();
            }
        } catch (NullPointerException npe) {
            logger.error("Can't get slack.legacyToken get it from https://api.slack.com/custom-integrations/legacy-tokens", npe);
        }
    }

    @GetMapping("{email}")
    public String slackInvite(@PathVariable String email) {

        String url = "https://slack.com/api/users.admin.invite?" +
                "email=" + email +
                "&token=" + INVITE_TOKEN;
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(new HttpGet(url))) {
            HttpEntity entity = response.getEntity();
            String json = EntityUtils.toString(entity);
            JSONObject result = new JSONObject(json);
        } catch (Throwable e) {
            logger.warn("Can't invite Client Slack profile", e);
        }
        return "redirect:/client";
    }
}
