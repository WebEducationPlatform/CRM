package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.MailingMessage;

import java.util.List;

public interface MailingMessageService extends CommonService<MailingMessage> {

    List<MailingMessage> getUserMail(long userId);

    List<MailingMessage> getUserByIdAndDate(long userId, int timeFrom, int timeTo);

    List<MailingMessage> getUserByDate(int timeFrom, int timeTo);

}
