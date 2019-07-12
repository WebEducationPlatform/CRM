package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.User;

/**
 * Определяет, кто из координаторов последним брал карточку клиента.
 * Этот координатор затем должен взять себе крточку клиента, созданного автоматически из
 * заявки, пришедшей из ВК, Gmail и др.
 * */

public interface UserFindTurnService {
    User getUserToOwnCard();
}
