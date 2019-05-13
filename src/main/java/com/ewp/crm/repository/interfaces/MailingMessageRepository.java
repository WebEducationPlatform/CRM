package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.ClientData;
import com.ewp.crm.models.MailingMessage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MailingMessageRepository extends CommonGenericRepository<MailingMessage> {

    List<MailingMessage> getAllByDateAfter(LocalDateTime date);

    List<MailingMessage> getAllByReadedMessageIsFalse();

    List<MailingMessage> findAllByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDateTime from, LocalDateTime to);

    List<MailingMessage> findAllByDateBetweenOrderByDateDesc(LocalDateTime from, LocalDateTime to);

    List<MailingMessage> findAllByUserId(Long userId);

    @Query("SELECT mail.date FROM MailingMessage mail")
    List<LocalDateTime> getTimeMailingMeassage();

    @Query("SELECT mail.clientsData FROM MailingMessage mail WHERE mail.id = :mailId")
    List<ClientData> getClientDataById(@Param("mailId") Long id);

}
