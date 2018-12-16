package com.ewp.crm.service.conversation;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.SocialProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JMConversationHelperImpl implements JMConversationHelper {

    private final int CHAT_MESSAGE_LIMIT = 40;

    private final List<JMConversation> conversations;

    private Map<ChatType, JMConversation> chatMap;

    @Autowired
    public JMConversationHelperImpl(List<JMConversation> conversations) {

        this.conversations = conversations;
    }

    @Override
    public void endChat(Client client) {
        client = null;
        if (chatMap != null) {
            chatMap.clear();
        }
    }

    @Override
    public ChatMessage sendMessage(ChatMessage message) {
        JMConversation conversation = chatMap.get(message.getChatType());

        if (conversation == null) {
            return null;
        }

        return conversation.sendMessage(message);
    }

    @Override
    public List<ChatMessage> getNewMessages(Client client) {
        List<ChatMessage> list = new LinkedList<>();
        for (Map.Entry<ChatType, JMConversation> entity : chatMap.entrySet()) {
            String chatId = "";
            List<ChatMessage> conversationMsg = entity.getValue().getNewMessages(chatId, CHAT_MESSAGE_LIMIT);
            list.addAll(0, conversationMsg);
        }

        list.sort(Comparator.comparing(ChatMessage::getTime));

        return list;
    }

    @Override
    public List<ChatMessage> getMessages(Client client) {
        List<ChatMessage> list = new LinkedList<>();
        for (Map.Entry<ChatType, JMConversation> entity : chatMap.entrySet()) {
            String chatId = "";
            List<ChatMessage> conversationMsg = entity.getValue().getMessages(chatId, CHAT_MESSAGE_LIMIT);
            list.addAll(0, conversationMsg);
        }

        list.sort(Comparator.comparing(ChatMessage::getTime));

        return list;
    }

    @Override
    public List<ChatMessage> getReadMessages(Client client) {
        return null;
    }

    @Override
    public List<Interlocutor> getInterlocutors(Client client) {
        return null;
    }

    @Override
    public List<Interlocutor> getUs(Client client) {
        return null;
    }


}
