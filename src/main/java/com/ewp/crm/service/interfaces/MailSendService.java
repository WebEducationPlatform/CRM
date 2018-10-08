package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.User;

import javax.mail.MessagingException;

public interface MailSendService {
    void validatorTestResult(String parseContent, Client client) throws MessagingException;

    void prepareAndSend(Long clientId, String templateText, String body, User principal);

    /**
     * Send email notification to client without any additional parameters and client history logging.
     * @param clientId recipient client id.
     * @param templateText Message template text.
     */
    void sendSimpleNotification(Long clientId, String templateText, String subject);

    void sendNotificationMessage(User userToNotify, String notificationMessage);

    void sendNotificationMessageYourself(String notificationMessage);

    void sendEmailInAllCases(Client client);

}
