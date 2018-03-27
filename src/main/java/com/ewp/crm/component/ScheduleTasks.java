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

    private void addClient(Client client) {
        Status newClientsStatus = statusService.get(1L);
        client.setStatus(newClientsStatus);
        client.setState(Client.State.NEW);
        clientService.addClient(client);
        logger.info("New client with id{} has added from VK", client.getId());
    }

    private void updateClient(Client newClient) {
        Client updateClient = clientService.getClientByVkId(newClient.getVkId());
        updateClient.setPhoneNumber(newClient.getPhoneNumber());
        updateClient.setEmail(newClient.getEmail());
        updateClient.setAge(newClient.getAge());
        updateClient.setSex(newClient.getSex());
        clientService.updateClient(updateClient);
        logger.info("Client with id{} has updated from VK", updateClient.getId());
    }

    @Scheduled(fixedRate = 10_000)
    private void handleRequestsFromVk() {
        try {
            Optional<List<String>> newMassages = vkUtil.getNewMassages();
            if (newMassages.isPresent()) {
                for (String message : newMassages.get()) {
                    try {
                        Client newClient = vkUtil.parseClientFromMessage(message);
                        if (clientService.getClientByVkId(newClient.getVkId()) == null) {
                            addClient(newClient);
                        }
                        if (clientService.getClientByVkId(newClient.getVkId()) != null) {
                            updateClient(newClient);
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

    @Scheduled(fixedRate = 10_000)
    private void handleRequestsFromVkCommunityMessages() {
        Optional<List<Long>> newUsers = vkUtil.getUsersIdFromCommunityMessages();
        if (newUsers.isPresent()) {
            for (Long id : newUsers.get()) {
                Client newClient = vkUtil.getClientFromVkId(id);
                if (newClient != null && clientService.getClientByVkId(newClient.getVkId()) == null) {
                    addClient(newClient);
                }
            }
        }
    }


}
