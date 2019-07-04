package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.User;

import javax.mail.MessagingException;

public interface MailSendService {
    void validatorTestResult(String parseContent, Client client) throws MessagingException;

    void prepareAndSend(Long clientId, String templateText, String body, User principal);

    /**
     * Send email notification to client without logging and additional body parameters.
     *
     * @param clientId     recipient client.
     * @param templateText email template text.
     */
    void sendSimpleNotification(Long clientId, String templateText);

    void sendNotificationMessage(User userToNotify, String notificationMessage);

    void sendReportToJavaMentorEmail(String report);

    void sendEmailInAllCases(Client client);

    void sendMessage(String email);
}
