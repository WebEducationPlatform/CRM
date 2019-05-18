package com.ewp.crm.service.conversation;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.conversation.ChatMessage;
import com.ewp.crm.models.conversation.ChatType;
import com.ewp.crm.models.conversation.Interlocutor;

import java.util.List;
import java.util.Map;

public interface JMConversationHelper {

    //желательно вызывать при окончании чата
    void endChat(Client client);

    //отправляет сообщение в определенный чат и возвращает новое сообщение
    ChatMessage sendMessage(ChatMessage message);

    //Получаем количество не прочитанных сообщений во всех чатах
    Map<Long, Integer> getCountOfNewMessages();

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
