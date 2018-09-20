package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.SlackProfile;
import com.ewp.crm.service.interfaces.SlackService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController(value = "/slack")
public class SlackRestController {

    private static final Logger logger = LoggerFactory.getLogger(SlackRestController.class);

    private final SlackService slackService;

    @Autowired
    public SlackRestController(SlackService slackService) {
        this.slackService = slackService;
    }

    @PostMapping
    public ResponseEntity<String> checkConnect(@RequestBody String body) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(body);

            //валидация ссылки куда Слак будет слать запросы. Выполняется 1 раз при смене ссылки в Слак
            //https://api.slack.com/apps/ACLA3QY72/event-subscriptions?
            //не забывать нажимать кнопку Save Changes.
            JsonNode challenge = jsonNode.get("challenge");
            if (challenge != null) {
                logger.info("Slack url_verification done");
                return new ResponseEntity<>(challenge.asText(), HttpStatus.OK);
            }

            //обрабатываем событие на вход юзера на канал.
            JsonNode event = jsonNode.get("event").get("type");
            if ("member_joined_channel".equals(event.asText())) {
                String slackHashName = event.get("event").get("user").asText();
                SlackProfile slackProfile = slackService.receiveClientSlackProfileBySlackHashName(slackHashName);
                logger.info("New member " + slackProfile.getDisplayName() + " " + slackProfile.getEmail() + " joined to general channel");
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private SlackProfile memberJoinSlackChannel(String slackHashName) {


        return new SlackProfile();
    }

}
