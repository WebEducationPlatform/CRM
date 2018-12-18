package com.ewp.crm.service.conversation;

import com.ewp.crm.models.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class JMConversationHelperImpl implements JMConversationHelper {

    private final int CHAT_MESSAGE_LIMIT = 40;

    private Map<ChatType, JMConversation> chatMap = new HashMap<>();

    @Autowired
    public JMConversationHelperImpl(List<JMConversation> conversations) {
        for (JMConversation conversation : conversations) {
            this.chatMap.put(conversation.getChatTypeOfConversation(), conversation);
        }
    }

    @Override
    public void endChat(Client client) {
        for (Map.Entry<ChatType, JMConversation> entity : chatMap.entrySet()) {
            entity.getValue().endChat(client);
        }
    }

    @Override
    public List<ChatMessage> sendMessage(ChatMessage message) {
        List<ChatMessage> result = new ArrayList<>();
        for (Map.Entry<ChatType, JMConversation> entity : chatMap.entrySet()) {
            result.add(entity.getValue().sendMessage(message));
        }
        return result;
    }

    @Override
    public List<ChatMessage> getNewMessages(Client client) {
        List<ChatMessage> result = new LinkedList<>();
        for (Map.Entry<ChatType, JMConversation> entity : chatMap.entrySet()) {
            List<ChatMessage> conversationMsg = entity.getValue().getNewMessages(client, CHAT_MESSAGE_LIMIT);
            result.addAll(conversationMsg);
        }
        result.sort(Comparator.comparing(ChatMessage::getTime));
        return result;
    }

    @Override
    public List<ChatMessage> getMessages(Client client) {
        List<ChatMessage> result = new LinkedList<>();
        for (Map.Entry<ChatType, JMConversation> entity : chatMap.entrySet()) {
            List<ChatMessage> conversationMsg = entity.getValue().getMessages(client, CHAT_MESSAGE_LIMIT);
            result.addAll(conversationMsg);
        }
        result.sort(Comparator.comparing(ChatMessage::getTime));
        return result;
    }

    @Override
    public Map<ChatType, String> getReadMessages(Client client) {
        Map<ChatType, String> result = new HashMap<>();
        for (Map.Entry<ChatType, JMConversation> entity : chatMap.entrySet()) {
            result.put(entity.getKey(), entity.getValue().getReadMessages(client));
        }
        return result;
    }

    @Override
    public List<Interlocutor> getInterlocutors(Client client) {
        List<Interlocutor> result = new LinkedList<>();
        for (Map.Entry<ChatType, JMConversation> entity : chatMap.entrySet()) {
            result.add(entity.getValue().getInterlocutor(client));
        }
        return result;
    }

    @Override
    public List<Interlocutor> getUs() {
        List<Interlocutor> result = new LinkedList<>();
        for (Map.Entry<ChatType, JMConversation> entity : chatMap.entrySet()) {
            result.add(entity.getValue().getMe());
        }
        return result;
    }

}
