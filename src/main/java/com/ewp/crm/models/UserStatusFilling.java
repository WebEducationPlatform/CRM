package com.ewp.crm.models;

import com.ewp.crm.service.interfaces.StatusService;
import com.ewp.crm.service.interfaces.UserStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class UserStatusFilling {
    @Autowired
    UserStatusService userStatusService;

    @Autowired
    StatusService statusService;

    @PostConstruct
    public void init() {
        List<UserStatus> userStatusList = userStatusService.getAll();
        if (userStatusList.isEmpty()) {
            List<Status> statusList = statusService.getAll();
            for (Status status : statusList) {
                userStatusService.addStatusForAllUsers(status.getId());
            }
        }
    }
}
