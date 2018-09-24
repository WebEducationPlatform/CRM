package com.ewp.crm.service.slack;

import com.ewp.crm.models.*;
import com.ewp.crm.repository.interfaces.SlackRepository;
import com.ewp.crm.service.impl.StudentServiceImpl;
import com.ewp.crm.service.interfaces.ClientHistoryService;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.SlackService;
import com.ewp.crm.service.interfaces.StatusService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@PropertySources(value = {
        @PropertySource("classpath:application.properties"),
        @PropertySource("file:./slack.properties")
})
public class SlackServiceImpl implements SlackService {

    private static Logger logger = LoggerFactory.getLogger(SlackServiceImpl.class);

    private final SlackRepository slackRepository;
    private final ClientService clientService;
    private final StudentServiceImpl studentService;
    private final StatusService statusService;
    private final ClientHistoryService clientHistoryService;

    private String LEGACY_TOKEN;

    @Autowired
    public SlackServiceImpl(Environment environment, SlackRepository slackRepository, ClientService clientService, StudentServiceImpl studentService, StatusService statusService, ClientHistoryService clientHistoryService) {
        try {
            this.LEGACY_TOKEN = environment.getRequiredProperty("slack.legacyToken");
            if (LEGACY_TOKEN.isEmpty()) {
                throw new NullPointerException();
            }
        } catch (NullPointerException npe) {
            logger.error("Can't get slack.legacyToken get it from https://api.slack.com/custom-integrations/legacy-tokens");
        }
        this.slackRepository = slackRepository;
        this.clientService = clientService;
        this.studentService = studentService;
        this.statusService = statusService;
        this.clientHistoryService = clientHistoryService;
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

    @Override
    public void memberJoinSlack(SlackProfile slackProfile) {
        Client client = checkMemberInBase(slackProfile);
        if (client.getEmail() != null) {
            client.setSlackProfile(slackProfile);
            slackProfile.setClient(client);
            Status oldClientStatus = client.getStatus();
            oldClientStatus.getClients().remove(client);
            Status newClientStatus = statusService.get(oldClientStatus.getId() + 1);
            if (newClientStatus != null) {
                client.setStatus(newClientStatus);
                newClientStatus.addClient(client);
                statusService.update(newClientStatus);
            }
            if (client.getStudent() == null) {
                studentService.addStudentForClient(client);
            }
            client.addHistory(clientHistoryService.createHistory(client.getName() + " "
                    + client.getLastName() + " joined to Slack nickname " + slackProfile.getDisplayName()));
            clientService.updateClient(client);
            logger.info("New member " + slackProfile.getDisplayName() + " "
                    + slackProfile.getEmail() + " joined to general channel");
        }
    }

    private Client checkMemberInBase(SlackProfile slackProfile) {
        Client client = clientService.getClientByEmail(slackProfile.getEmail());
        if (client != null) {
            return client;
        } else {
            logger.error(slackProfile.getDisplayName() + " not a client! Joined to Slack general channel!");
            return new Client();
        }
    }
}
