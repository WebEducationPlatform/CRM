package com.ewp.crm.service.interfaces;

import org.drinkless.tdlib.TdApi;
import java.io.IOException;
import java.util.Optional;

public interface TelegramService {

    void sendAuthPhone(String phone);

    void sentAuthCode(String smsCode);

    boolean isAuthenticated();

    boolean isTdlibInstalled();

    TdApi.Messages getChatMessages(long chatId, int limit);

    TdApi.Messages getUnreadMessagesFromChat(long chatId, int limit);

    TdApi.Message sendChatMessage(long chatId, String text);

    Optional<TdApi.Chat> getChat(long chatId);

    TdApi.Chats getChats();

    int getClientIdByPhone(String phone);

    void closeChat(long chatId);

    TdApi.User getTgMe();

    TdApi.User getUserById(int userId);

    TdApi.File getFileById(int fileId);

    String downloadFile(TdApi.File file) throws IOException;

    void logout();
}
