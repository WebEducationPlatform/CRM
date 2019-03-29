package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Client;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.SlackService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

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
    private final ClientService clientService;
    private final String inviteToken;

    @Autowired
    public SlackRestController(ClientService clientService, Environment environment, SlackService slackService) {
        this.inviteToken = environment.getRequiredProperty("slack.legacyToken");
        if (inviteToken.isEmpty()) {
            logger.warn("Can't get slack.legacyToken get it from https://api.slack.com/custom-integrations/legacy-tokens");
        }
        this.slackService = slackService;
        this.clientService = clientService;
    }

    @GetMapping("/find/client/{clientId}")
    public ResponseEntity<String> findClientSlackProfile(@PathVariable long clientId) {
        Optional<Client> client = clientService.getClientByID(clientId);
        if (client.isPresent() && client.get().getStudent() != null) {
            return findStudentSlackProfile(client.get().getStudent().getId());
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/find/student/{studentId}")
    public ResponseEntity<String> findStudentSlackProfile(@PathVariable long studentId) {
        if (slackService.tryLinkSlackAccountToStudent(studentId)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/get/emails")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
    public ResponseEntity<String> getAllEmailsFromSlack() {
        String url = "https://slack.com/api/users.list?token=" + inviteToken;
        String result = "Error";
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(new HttpGet(url))) {
            HttpEntity entity = response.getEntity();
            Optional<String> emails = slackService.getEmailListFromJson(EntityUtils.toString(entity));
            if (emails.isPresent()) {
                result = emails.get();
            }
        } catch (Throwable e) {
            logger.warn("Can't parse emails from Slack", e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "text/plain;charset=UTF-8");
        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }
}
