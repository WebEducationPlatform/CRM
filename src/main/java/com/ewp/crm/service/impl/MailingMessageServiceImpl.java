package com.ewp.crm.service.impl;


import com.ewp.crm.models.ClientData;
import com.ewp.crm.models.MailingMessage;
import com.ewp.crm.repository.interfaces.MailingMessageRepository;
import com.ewp.crm.service.interfaces.MailingMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class MailingMessageServiceImpl extends CommonServiceImpl<MailingMessage> implements MailingMessageService {

    private final MailingMessageRepository mailingMessageRepository;

    @Autowired
    public MailingMessageServiceImpl(MailingMessageRepository mailingMessageRepository) {
        this.mailingMessageRepository = mailingMessageRepository;
    }


    @Override
    public List<MailingMessage> getUserMail(long userId) {
        return mailingMessageRepository.getUserMail(userId);
    }

    @Override
    public List<MailingMessage> getUserByIdAndDate(long userId, int timeFrom, int timeTo) {
        return mailingMessageRepository.getUserByIdAndDate(userId, timeFrom, timeTo);
    }

    @Override
    public List<MailingMessage> getUserByDate(int timeFrom, int timeTo) {
        return mailingMessageRepository.getUserByDate(timeFrom, timeTo);
    }

    public List<ClientData> getClientDataById(Long id) {
        return mailingMessageRepository.getClientDataById(id);
    }

}
