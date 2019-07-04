package com.ewp.crm.service.interfaces;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface SlackService {
    ResponseEntity<String> inviteToWorkspace(String name, String lastName, String email);

    ResponseEntity<String> inviteToWorkspace(String email);

    Optional<String> getAllEmailsFromSlack();

    Optional<String> getAllIdsFromSlack();

    boolean tryLinkSlackAccountToStudent(long studentId);

    void tryLinkSlackAccountToAllStudents();

    boolean trySendMessageToSlackUser(String slackUserId, String text);

    boolean trySendMessageToAllSlackUsers(String text);

    boolean trySendSlackMessageToStudent(long clientId, String text);

    boolean trySendMessageToAllStudents(String text);

    String getSlackWorkspaceUrl();

    Optional<String> getChatIdForSlackUser(String slackUserId);

    List getAllClientsFromWorkspace();



}
