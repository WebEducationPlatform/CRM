package com.ewp.crm.service.impl;

import com.ewp.crm.models.ClientData;
import com.ewp.crm.models.MailingMessage;
import com.ewp.crm.repository.interfaces.MailingMessageRepository;
import com.ewp.crm.service.interfaces.MailingMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class MailingMessageServiceImpl extends CommonServiceImpl<MailingMessage> implements MailingMessageService {

    private final MailingMessageRepository mailingMessageRepository;

    @Autowired
    public MailingMessageServiceImpl(MailingMessageRepository mailingMessageRepository) {
        this.mailingMessageRepository = mailingMessageRepository;
    }

    @Override
    public List<MailingMessage> getMailingMessageByUserId(Long userId) {
        return mailingMessageRepository.findAllByUserId(userId);
    }

    @Override
    public List<MailingMessage> getMailingMessageByUserIdAndDate(Long userId, LocalDateTime from, LocalDateTime to) {
        return mailingMessageRepository.findAllByUserIdAndDateBetweenOrderByDateDesc(userId, from, to);
    }

    @Override
    public List<MailingMessage> getMailingMessageByDate(LocalDateTime from, LocalDateTime to) {
        return mailingMessageRepository.findAllByDateBetweenOrderByDateDesc(from, to);
    }

    public List<ClientData> getClientDataById(Long id) {
        return mailingMessageRepository.getClientDataById(id);
    }

}
