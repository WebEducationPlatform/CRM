package com.ewp.crm.component;

import com.ewp.crm.component.util.VKUtil;
import com.ewp.crm.controllers.UserController;
import com.ewp.crm.exceptions.parse.ParseClientException;
import com.ewp.crm.exceptions.util.VKAccessTokenException;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.Status;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.StatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableScheduling
public class ScheduleTasks {

    private VKUtil vkUtil;

    private ClientService clientService;

    private StatusService statusService;

    private static Logger logger = LoggerFactory.getLogger(ScheduleTasks.class);

    @Autowired
    public ScheduleTasks(VKUtil vkUtil, ClientService clientService, StatusService statusService) {
        this.vkUtil = vkUtil;
        this.clientService = clientService;
        this.statusService = statusService;
    }

    @Scheduled(fixedRate = 10_000)
    private void handleRequestsFromVk() {
        try {
            List<String> newMassages = vkUtil.getNewMassages();
            for (String message : newMassages) {
                Client newClient = null;
                try {
                    newClient = vkUtil.parseClientFromMessage(message);
                } catch (ParseClientException e) {
                    logger.error(e.getMessage());
                }
                if ((newClient != null) && (clientService.getClientByEmail(newClient.getEmail()) == null)) {
                    Status newClientsStatus = statusService.get("New clients");
                    newClient.setStatus(newClientsStatus);
                    clientService.addClient(newClient);
                    logger.info("New client with id{} has added from VK", newClient.getId());
                }
            }
        } catch (VKAccessTokenException ex) {
            logger.error(ex.getMessage());
        }
    }
}
