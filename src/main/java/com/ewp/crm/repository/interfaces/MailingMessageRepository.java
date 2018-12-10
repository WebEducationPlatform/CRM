package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.MailingMessage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MailingMessageRepository extends CommonGenericRepository<MailingMessage>{
    List<MailingMessage> getAllByDateAfter(LocalDateTime date);
    List<MailingMessage> getAllByReadedMessageIsFalse();

    @Query("SELECT mail FROM MailingMessage mail WHERE mail.userId =:userId ")
    List<MailingMessage> getUserMail(@Param("userId") long userId);

    @Query("SELECT mail FROM MailingMessage mail WHERE mail.userId =:userId AND EXTRACT (day from mail.date) =:time")
    List<MailingMessage> getUserByMailAndDate(@Param("userId") long userId, @Param("time") int time);

    @Query("SELECT mail FROM MailingMessage mail WHERE EXTRACT (day from mail.date) =:time")
    List<MailingMessage> getUserByDate(@Param("time") int time);

    @Query("SELECT mail.date FROM MailingMessage mail")
    List<LocalDateTime> getTimeMailingMeassage();

}
