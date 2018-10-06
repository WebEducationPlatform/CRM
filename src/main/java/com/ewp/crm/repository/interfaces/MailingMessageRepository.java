package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.MailingMessage;
import java.time.LocalDateTime;
import java.util.List;

public interface MailingMessageRepository extends CommonGenericRepository<MailingMessage>{
    List<MailingMessage> getAllByDateAfter(LocalDateTime date);
    List<MailingMessage> getAllByReadedMessageIsFalse();
}
