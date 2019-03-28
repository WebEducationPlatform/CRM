package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.SlackProfile;

public interface SlackService {
    void memberJoinSlack(SlackProfile slackProfile);
    SlackProfile receiveClientSlackProfileBySlackHashName(String slackHashName);

    String getEmailListFromJson(String json);
}
