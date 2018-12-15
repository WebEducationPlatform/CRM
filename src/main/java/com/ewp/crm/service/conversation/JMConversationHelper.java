package com.ewp.crm.service.conversation;

import com.ewp.crm.models.Client;

import java.util.List;

public interface JMConversationHelper {

    //вызывается при старе нового чата, по клиенту стартуют те чаты на которые у клиента есть профили.
    void startNewChat(Client client);

    //желательно вызывать при окончании чата
    void endChat(Client client);

    //отправляет сообщение в определенный чат и возвращает новое сообщение
    ChatMessage sendMessage(ChatMessage message);

    //помечает данное сообщение как прочитанное в определенном чате
    ChatMessage markMessageAsRead(ChatMessage message, ChatType chatType);

    //Получаем не прочитынные сообщения из всех чатов
    List<ChatMessage> getNewMessages();

    //получаем последнии сообщения их всех чатов.
    //на выходе лист с максимум count*N элементов, N - количество чатов, count - число получаемых сообщений
    List<ChatMessage> getMessages();

}
