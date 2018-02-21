package com.ewp.crm.component;

import com.ewp.crm.component.util.VKUtil;
import com.ewp.crm.exceptions.parse.ParseClientException;
import com.ewp.crm.models.Client;
import com.ewp.crm.service.interfaces.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableScheduling
public class ScheduleTasks {

    @Autowired
    private VKUtil vkUtil;

    @Autowired
    private ClientService clientService;

    @Scheduled(fixedRate = 600_000)
    private void handleRequestsFromVk() {
        List<String> newMassages = vkUtil.getNewMassages();
        for (String message: newMassages) {
            Client newClient = null;
            try {
                newClient = vkUtil.parseClientFromMessage(message);
            } catch (ParseClientException e) {
                e.printStackTrace();
            }
            if ((newClient != null) && (clientService.getClientByEmail(newClient.getEmail()) == null)) {
                clientService.addClient(newClient);
            }
        }
    }
}
