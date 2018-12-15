package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.ClientData;
import com.ewp.crm.models.MailingMessage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MailingMessageRepository extends CommonGenericRepository<MailingMessage>{
    List<MailingMessage> getAllByDateAfter(LocalDateTime date);
    List<MailingMessage> getAllByReadedMessageIsFalse();

    @Query("SELECT mail FROM MailingMessage mail WHERE mail.userId = :userId ")
    List<MailingMessage> getUserMail(@Param("userId") long userId);

    @Query("SELECT mail FROM MailingMessage mail WHERE mail.userId = :userId AND EXTRACT (day FROM mail.date) BETWEEN :timeFrom AND :timeTo")
    List<MailingMessage> getUserByIdAndDate(@Param("userId") long userId, @Param("timeFrom") int timeFrom, @Param("timeTo") int timeTo);

    @Query("SELECT mail FROM MailingMessage mail WHERE EXTRACT (day FROM mail.date) BETWEEN :timeFrom AND :timeTo")
    List<MailingMessage> getUserByDate(@Param("timeFrom") int timeFrom, @Param("timeTo") int timeTo);

    @Query("SELECT mail.date FROM MailingMessage mail")
    List<LocalDateTime> getTimeMailingMeassage();

    @Query("SELECT mail.clientsData FROM MailingMessage mail where mail.id = :mailId")
    List<ClientData> getClientDataById(@Param("mailId") Long id);

}
