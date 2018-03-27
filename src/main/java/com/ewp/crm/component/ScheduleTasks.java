package com.ewp.crm.component;

import com.ewp.crm.component.util.VKUtil;
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
import java.util.Optional;

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
            Optional<List<String>> newMassages = vkUtil.getNewMassages();
            if (newMassages.isPresent()) {
                for (String message : newMassages.get()) {
                    try {
                        Client newClient = vkUtil.parseClientFromMessage(message);
                        if (clientService.getClientByEmail(newClient.getEmail()) == null) {
                            Status newClientsStatus = statusService.get(1L);
                            newClient.setStatus(newClientsStatus);
                            clientService.addClient(newClient);
                            logger.info("New client with id{} has added from VK", newClient.getId());
                        }
                    } catch (ParseClientException e) {
                        logger.error(e.getMessage());
                    }
                }
            }
        } catch (VKAccessTokenException ex) {
            logger.error(ex.getMessage());
        }

    }
}
