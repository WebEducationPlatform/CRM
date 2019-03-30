package com.ewp.crm.service.interfaces;

import java.util.Optional;

public interface SlackService {
    Optional<String> getAllEmailsFromSlack();
    boolean tryLinkSlackAccountToStudent(long studentId);
    boolean trySendMessageToSlackUser(String slackUserId, String text);
    boolean trySendMessageToAllSlackUsers(String text);
    boolean trySendSlackMessageToStudent(long studentId, String text);
}
