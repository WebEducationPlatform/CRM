package com.ewp.crm.service.conversation;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.conversation.ChatMessage;
import com.ewp.crm.models.conversation.ChatType;
import com.ewp.crm.models.conversation.Interlocutor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface JMConversation {

    //каждый чат должен возвращать тот тип чата который он реализует.
    // Так как связывание делает спринг, надо их как то отличать.
    ChatType getChatTypeOfConversation();

    //желательно вызывать при окончании чата
    void endChat(Client client);

    //отправляет сообщение и возвращает новое сообщение
    ChatMessage sendMessage(ChatMessage message);

    //Получаем количество не прочитынных сообщения в разрезе client'ов
    Map<Client, Integer> getCountOfNewMessages();

    //Получаем не прочитынные сообщения
    List<ChatMessage> getNewMessages(Client client, int count);

    //получаем последнии count сообщений из чата
    List<ChatMessage> getMessages(Client client, int count);

    //Получить список сообщений, прочитанных собеседником
    String getReadMessages(Client client);

    //получить собеседника по ID сущности
    Optional<Interlocutor> getInterlocutor(Client client);

    //Получить текущего залогиненого пользователя по ID сущности
    Optional<Interlocutor> getMe();
}
