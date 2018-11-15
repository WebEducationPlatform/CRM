package com.ewp.crm.service.interfaces;

import org.drinkless.tdlib.TdApi;

public interface TelegramService {

    void sendAuthPhone(String phone);

    void sentAuthCode(String smsCode);

    boolean isAuthenticated();

    boolean isTdlibInstalled();

    TdApi.Messages getChatMessages(long chatId, int limit);

    void sendChatMessage(long chatId, String text);

    void logout();
}
