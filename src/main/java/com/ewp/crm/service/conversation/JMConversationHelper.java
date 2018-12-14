package com.ewp.crm.service.conversation;

import com.ewp.crm.models.Client;

import java.util.List;

public interface JMConversationHelper {

    //вызывается при старе нового чата, по клиенту стартуют те чаты на которые у клиента есть профили.
    void startNewChat(Client client);

    //желательно вызывать при окончании чата
    void endChat();

    //отправляет сообщение в определенный чат и возвращает новое сообщение
    ChatMessage sendMessage(String text, ChatType chatType);

    //помечает данное сообщение как прочитанное в определенном чате
    ChatMessage markMessageAsRead(ChatMessage message, ChatType chatType);

    //Получаем не прочитынные сообщения из всех чатов
    List<ChatMessage> getNewMessages();

    //получаем последнии count сообщений их всех чатов.
    //на выходе лист с максимум count*N элементов, N - количество чатов.
    List<ChatMessage> getMessages(int count);

}
