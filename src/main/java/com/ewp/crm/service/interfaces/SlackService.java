package com.ewp.crm.service.interfaces;

import java.util.Optional;

public interface SlackService {
    Optional<String> getAllEmailsFromSlack();
    Optional<String> getAllIdsFromSlack();
    boolean tryLinkSlackAccountToStudent(long studentId);
    boolean tryLinkSlackAccountToStudent(long studentId, String slackAllUsersJsonResponse);
    void tryLinkSlackAccountToAllStudents();
    boolean trySendMessageToSlackUser(String slackUserId, String text);
    boolean trySendMessageToAllSlackUsers(String text);
    boolean trySendSlackMessageToStudent(long studentId, String text);
    boolean trySendMessageToAllStudents(String text);
}
