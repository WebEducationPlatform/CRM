package com.ewp.crm.service.interfaces;

import org.springframework.core.env.Environment;

import java.util.Optional;

public interface SlackService {
    boolean inviteToWorkspace(String name, String lastName, String email);
    boolean inviteToWorkspace(String email);
    Optional<String> getAllEmailsFromSlack();
    Optional<String> getAllIdsFromSlack();
    boolean tryLinkSlackAccountToStudent(long studentId);
    boolean tryLinkSlackAccountToStudent(long studentId, String slackAllUsersJsonResponse);
    void tryLinkSlackAccountToAllStudents();
    boolean trySendMessageToSlackUser(String slackUserId, String text);
    boolean trySendMessageToAllSlackUsers(String text);
    boolean trySendSlackMessageToStudent(long clientId, String text);
    boolean trySendMessageToAllStudents(String text);
    String getSlackWorkspaceUrl();
    Optional<String> getChatIdForSlackUser(String slackUserId);
    void setAppToken(String number, Environment environment);
}
