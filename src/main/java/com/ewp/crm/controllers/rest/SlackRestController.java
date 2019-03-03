package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.ProjectProperties;
import com.ewp.crm.models.SlackProfile;
import com.ewp.crm.service.interfaces.ProjectPropertiesService;
import com.ewp.crm.service.interfaces.SlackService;
import com.ewp.crm.service.interfaces.StatusService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * TODO before start.
 * Docs about slack functions installation
 * 1. Goto https://api.slack.com/apps
 * 2. Choice app for Workspace (JavaMentor or something else)
 * 3. Goto Features -> Event Subscriptions
 * 4. Turn ON "Enable Events'
 * TODO after start
 * 5. Put at "Request URL" app-IP/slack or app-URL/slack
 * 6. Wait for verify
 * 7. Add to "Add Bot User Event" event with name "member_joined_channel"
 * 8. TODO Проверить и дописать инструкцию. Продублировать.
 */

@RestController
@RequestMapping("/slack")
public class SlackRestController {

    private static final Logger logger = LoggerFactory.getLogger(SlackRestController.class);

    private final SlackService slackService;
    private final StatusService statusService;
    private final ProjectPropertiesService propertiesService;
    private String inviteToken;

    @Autowired
    public SlackRestController(Environment environment,
                               SlackService slackService,
                               StatusService statusService,
                               ProjectPropertiesService propertiesService) {
        try {
            this.inviteToken = environment.getRequiredProperty("slack.legacyToken");
            if (inviteToken.isEmpty()) {
                throw new NullPointerException();
            }
        } catch (NullPointerException npe) {
            logger.error("Can't get slack.legacyToken get it from https://api.slack.com/custom-integrations/legacy-tokens", npe);
        }
        this.slackService = slackService;
        this.statusService = statusService;
        this.propertiesService = propertiesService;
    }

    @PostMapping
    public ResponseEntity<String> interactionsWithSlack(@RequestBody String body) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(body);

            //валидация ссылки куда Слак будет слать запросы. Выполняется 1 раз при смене ссылки в Слак
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
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
    public ResponseEntity<String> getAllStatusForStudents() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ObjectMapper mapper = new ObjectMapper()
                .configure(SerializationFeature.INDENT_OUTPUT, true);
        try {
            mapper.writeValue(out, statusService.getAllStatusesForStudents());
        } catch (IOException e) {
            logger.warn("Cant wrap json", e);
        }
        final byte[] data = out.toByteArray();
        return new ResponseEntity<>(new String(data), HttpStatus.OK);
    }

    @GetMapping(value = "/set/default/{statusId}")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
    public ResponseEntity setDefaultStatus(@PathVariable("statusId") Long id) {
        ProjectProperties projectProperties = propertiesService.get();
        if (projectProperties == null) {
            propertiesService.saveAndFlash(new ProjectProperties());
            projectProperties = propertiesService.get();
        }
        projectProperties.setDefaultStatusId(id);
        propertiesService.saveAndFlash(projectProperties);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/get/emails")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
    public ResponseEntity<String> getAllEmailsFromSlack() {
        String url = "https://slack.com/api/users.list?token=" + inviteToken;
        String result = "Error";
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(new HttpGet(url))) {
            HttpEntity entity = response.getEntity();
            result = slackService.getEmailListFromJson(EntityUtils.toString(entity));
        } catch (Throwable e) {
            logger.warn("Can't parse emails from Slack", e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "text/plain;charset=UTF-8");
        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }

    @GetMapping("{email}")
    public ResponseEntity<String> slackInvite(@PathVariable String email) {

        String url = "https://slack.com/api/users.admin.invite?" +
                "email=" + email +
                "&token=" + inviteToken;
        String json = null;
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(new HttpGet(url))) {
            HttpEntity entity = response.getEntity();
            json = EntityUtils.toString(entity);
        } catch (Throwable e) {
            logger.warn("Can't invite Client Slack profile", e);
        }
        return new ResponseEntity<>(json, HttpStatus.OK);
    }
}
