package com.ewp.crm.service.conversation;

import java.util.List;

public interface JMConversation {

    //каждый чат должен возвращать тот тип чата который он реализует.
    // Так как связывание делает спринг, надо их как то отличать.
    ChatType getChatTypeOfConversation();

    //желательно вызывать при окончании чата
    void endChat(String chatId);

    //отправляет сообщение и возвращает новое сообщение
    ChatMessage sendMessage(ChatMessage message);

    //Получаем не прочитынные сообщения
    List<ChatMessage> getNewMessages(String chatId, int count);

    //получаем последнии count сообщений из чата
    List<ChatMessage> getMessages(String chatId, int count);

    //Получить список сообщений, прочитанных собеседником
    List<ChatMessage> getReadMessages(String chatId);

    //получить собеседника по ID сущности
    Interlocutor getInterlocutor(String recipientId);

    //Получить текущего залогиненого пользователя по ID сущности
    Interlocutor getMe(String recipientId);
}
