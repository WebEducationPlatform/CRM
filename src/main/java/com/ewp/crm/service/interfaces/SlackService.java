package com.ewp.crm.service.interfaces;

import java.util.Optional;

public interface SlackService {
    Optional<String> getEmailListFromJson(String json);
    boolean tryLinkSlackAccountToStudent(long studentId);
    Optional<String> receiveAllClientsFromWorkspace();
}
