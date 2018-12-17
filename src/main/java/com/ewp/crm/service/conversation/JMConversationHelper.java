package com.ewp.crm.service.conversation;

import com.ewp.crm.models.Client;

import java.util.List;
import java.util.Map;

public interface JMConversationHelper {

    //желательно вызывать при окончании чата
    void endChat(Client client);

    //отправляет сообщение в определенный чат и возвращает новое сообщение
    ChatMessage sendMessage(ChatMessage message);

    //помечает данное сообщение как прочитанное в определенном чате
    ChatMessage markMessageAsRead(ChatMessage message);

    //Получаем не прочитынные сообщения из всех чатов
    //Создаем чат и открывем тут же.
    List<ChatMessage> getNewMessages(Client client);

    //получаем последнии сообщения их всех чатов.
    //на выходе лист с максимум count*N элементов, N - количество чатов, count - число получаемых сообщений
    List<ChatMessage> getMessages(Client client);

    //Получить список сообщений, прочитанных собеседником
    Map<ChatType, String> getReadMessages(Client client);

    //получить список всех собеседников всех возможных типов
    List<Interlocutor> getInterlocutors(Client client);

    //Получить список всех собеседников всех возможных типов текущего пользователя
    List<Interlocutor> getUs();
}
