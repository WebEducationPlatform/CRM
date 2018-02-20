package com.ewp.crm.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableScheduling
public class ScheduleTasks {

    @Autowired
    private VKNotifier vkNotifier;

    @Scheduled(fixedRate = 5000)
    private void handleRequestsFromVk() {
        List<String> newMassages = vkNotifier.getNewMassages();
        for (String message: newMassages) {
            //TODO replace with parse message
            System.out.println("Message from VK: " + message);
        }
    }
}
