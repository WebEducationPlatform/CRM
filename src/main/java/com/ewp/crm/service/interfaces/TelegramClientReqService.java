package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.TelegramClientReq;

import java.util.Optional;

public interface TelegramClientReqService extends CommonService<TelegramClientReq> {
    Optional<TelegramClientReq> getByUserId(Integer userId);
}
