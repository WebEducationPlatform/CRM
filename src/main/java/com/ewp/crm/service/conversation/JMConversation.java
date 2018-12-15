package com.ewp.crm.service.conversation;

import java.util.List;

public interface JMConversation {

    //каждый чат должен возвращать тот тип чата который он реализует. Так как связывание делает спринг, надо их как то отличать.
    ChatType getChatTypeOfConversation();

    //вызывается при старе нового чата, id - либо ссылка на профиль, либо ид в зависимости от того что хранится у клиента в соц профилях
    void startNewChat(String id);

    //желательно вызывать при окончании чата
    void dropChat(Chat chat);

    //отправляет сообщение и возвращает новое сообщение
    ChatMessage sendMessage(ChatMessage message);

    //помечает данное сообщение как прочитанное
    ChatMessage markMessageAsRead(ChatMessage message);

    //Получаем не прочитынные сообщения
    List<ChatMessage> getNewMessages();

    //получаем последнии count сообщений из чата
    List<ChatMessage> getMessages(int count);

}
