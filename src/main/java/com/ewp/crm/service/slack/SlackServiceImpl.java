package com.ewp.crm.service.slack;

import com.ewp.crm.models.SlackProfile;
import com.ewp.crm.service.interfaces.SlackService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
@PropertySources(value = {
        @PropertySource("classpath:application.properties"),
        @PropertySource("file:./slack.properties")
})
public class SlackServiceImpl implements SlackService {

    private static Logger logger = LoggerFactory.getLogger(SlackServiceImpl.class);

    private String LEGACY_TOKEN;

    @Autowired
    public SlackServiceImpl(Environment environment) {
        try {
            this.LEGACY_TOKEN = environment.getRequiredProperty("slack.legacyToken");
            if (LEGACY_TOKEN.isEmpty()) {
                throw new NullPointerException();
            }
        } catch (NullPointerException npe) {
            logger.error("Can't get slack.legacyToken get it from https://api.slack.com/custom-integrations/legacy-tokens");
        }
    }

    @Override
    public void memberJoinSlack(SlackProfile slackProfile) {

    }

    @Override
    public SlackProfile receiveClientSlackProfileBySlackHashName(String slackHashName) {

        try {
            String url = "https://slack.com/api/users.info?token=" + LEGACY_TOKEN + "&user=" + slackHashName;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            System.out.println(response.toString());
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode actualObj = objectMapper.readTree(response.toString()).get("user").get("profile");
            return objectMapper.treeToValue(actualObj, SlackProfile.class);

        } catch (IOException e) {
            logger.error("Can't receive Client Slack profile", e);
            e.printStackTrace();
        }
        return new SlackProfile();
    }
}
