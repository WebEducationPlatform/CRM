package com.ewp.crm.service.interfaces;

import org.drinkless.tdlib.TdApi;
import java.io.IOException;

public interface TelegramService {

    void sendAuthPhone(String phone);

    void sentAuthCode(String smsCode);

    boolean isAuthenticated();

    boolean isTdlibInstalled();

    TdApi.Messages getChatMessages(long chatId, int limit);

    TdApi.Messages getUnreadMessagesFromChat(long chatId, int limit);

    TdApi.Message sendChatMessage(long chatId, String text);

    TdApi.Chat getChat(long chatId);

    int getClientIdByPhone(String phone);

    void closeChat(long chatId);

    TdApi.User getMe();

    TdApi.User getUserById(int userId);

    TdApi.File getFileById(int fileId);

    String downloadFile(TdApi.File file) throws IOException;

    void logout();
}
