package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.SlackProfile;
import com.ewp.crm.models.Status;
import com.ewp.crm.service.interfaces.SlackService;
import com.ewp.crm.service.interfaces.StatusService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/slack")
public class SlackRestController {

    private static final Logger logger = LoggerFactory.getLogger(SlackRestController.class);

    private final SlackService slackService;
    private final StatusService statusService;

    @Autowired
    public SlackRestController(SlackService slackService, StatusService statusService) {
        this.slackService = slackService;
        this.statusService = statusService;
    }

    @PostMapping
    public ResponseEntity<String> interactionsWithSlack(@RequestBody String body) {
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
                String slackHashName = jsonNode.get("event").get("user").asText();
                SlackProfile slackProfile = slackService.receiveClientSlackProfileBySlackHashName(slackHashName);
                slackService.memberJoinSlack(slackProfile);
            }
        } catch (IOException e) {
            logger.warn("Cant read json form Slack", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/get/students/statuses")
    public ResponseEntity<String> getAllStatusForStudents() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ObjectMapper mapper = new ObjectMapper()
                .configure(SerializationFeature.INDENT_OUTPUT, true);
        try {
            mapper.writeValue(out, statusService.getAllStatusesForStudents());
        } catch (IOException e) {
            e.printStackTrace();
        }
        final byte[] data = out.toByteArray();
        return new ResponseEntity<>(new String(data), HttpStatus.OK);
    }

    @GetMapping(value = "/set/default/{statusId}")
    public ResponseEntity setDefaultStatus(@PathVariable("statusId") Long id) {

        return ResponseEntity.ok(HttpStatus.OK);
    }
}
