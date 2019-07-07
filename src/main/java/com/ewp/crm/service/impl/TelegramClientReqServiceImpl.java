package com.ewp.crm.service.impl;

import com.ewp.crm.models.TelegramClientReq;
import com.ewp.crm.repository.interfaces.TelegramClientReqRepository;
import com.ewp.crm.service.interfaces.TelegramClientReqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TelegramClientReqServiceImpl extends CommonServiceImpl<TelegramClientReq> implements TelegramClientReqService {

    private final TelegramClientReqRepository telegramClientReqRepository;

    @Autowired
    public TelegramClientReqServiceImpl(TelegramClientReqRepository telegramClientReqRepository) {
        this.telegramClientReqRepository = telegramClientReqRepository;
    }

    public Optional<TelegramClientReq> getByUserId(Integer userId) {
        return telegramClientReqRepository.getTelegramClientReqByUserId(userId);
    }
}
