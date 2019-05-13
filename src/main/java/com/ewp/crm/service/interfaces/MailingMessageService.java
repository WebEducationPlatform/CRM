package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.ClientData;
import com.ewp.crm.models.MailingMessage;

import java.time.LocalDateTime;
import java.util.List;

public interface MailingMessageService extends CommonService<MailingMessage> {

    List<MailingMessage> getMailingMessageByUserId(Long userId);

    List<MailingMessage> getMailingMessageByUserIdAndDate(Long userId, LocalDateTime from, LocalDateTime to);

    List<MailingMessage> getMailingMessageByDate(LocalDateTime from, LocalDateTime to);

    List<ClientData> getClientDataById(Long id);

}
