package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.TelegramClientReq;

import java.util.Optional;

public interface TelegramClientReqRepository extends CommonGenericRepository<TelegramClientReq> {
    Optional<TelegramClientReq> getTelegramClientReqByUserId(Integer userId);
}
