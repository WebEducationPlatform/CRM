package com.ewp.crm.controllers.rest;
import com.ewp.crm.service.interfaces.ProjectPropertiesService;
import com.ewp.crm.service.interfaces.SlackService;
import com.ewp.crm.service.interfaces.StatusService;
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
    private String inviteToken;

    @Autowired
    public SlackRestController(Environment environment,
                               SlackService slackService) {
        try {
            this.inviteToken = environment.getRequiredProperty("slack.legacyToken");
            if (inviteToken.isEmpty()) {
                throw new NullPointerException();
            }
        } catch (NullPointerException npe) {
            logger.error("Can't get slack.legacyToken get it from https://api.slack.com/custom-integrations/legacy-tokens", npe);
        }
        this.slackService = slackService;
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
}
