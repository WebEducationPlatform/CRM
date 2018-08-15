package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.MailingMessage;

import org.joda.time.LocalDateTime;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MailingMessageRepository extends CommonGenericRepository<MailingMessage>{
    List<MailingMessage> getAllByDateAfter(LocalDateTime date);
    List<MailingMessage> getAllByReadedMessageIsFalse();
}
