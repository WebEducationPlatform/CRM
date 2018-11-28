package com.ewp.crm.service.interfaces;

import org.drinkless.tdlib.TdApi;

public interface TelegramService {

    void sendAuthPhone(String phone);

    void sentAuthCode(String smsCode);

    boolean isAuthenticated();

    boolean isTdlibInstalled();

    TdApi.Messages getChatMessages(long chatId, int limit);

    TdApi.Messages getUnreadMessagesFromChat(long chatId, long lastMessageId, int limit);

    void sendChatMessage(long chatId, String text);

    TdApi.Chat getChat(long chatId);

    void closeChat(long chatId);

    void logout();
}
