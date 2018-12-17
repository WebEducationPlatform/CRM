package com.ewp.crm.service.conversation;

import com.ewp.crm.models.Client;

import java.util.List;

public interface JMConversation {

    //каждый чат должен возвращать тот тип чата который он реализует.
    // Так как связывание делает спринг, надо их как то отличать.
    ChatType getChatTypeOfConversation();

    //желательно вызывать при окончании чата
    void endChat(Client client);

    //отправляет сообщение и возвращает новое сообщение
    ChatMessage sendMessage(ChatMessage message);

    //Получаем не прочитынные сообщения
    List<ChatMessage> getNewMessages(String chatId, int count);

    //получаем последнии count сообщений из чата
    List<ChatMessage> getMessages(String chatId, int count);

    //Получить список сообщений, прочитанных собеседником
    String getReadMessages(Client client);

    //получить собеседника по ID сущности
    Interlocutor getInterlocutor(Client client);

    //Получить текущего залогиненого пользователя по ID сущности
    Interlocutor getMe();
}
