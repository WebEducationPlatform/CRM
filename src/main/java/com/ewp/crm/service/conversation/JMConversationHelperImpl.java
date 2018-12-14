package com.ewp.crm.service.conversation;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.SocialProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JMConversationHelperImpl implements JMConversationHelper {

    private final int messageLimitInConversation = 40;

    private Client client;

    private final List<JMConversation> conversations;

    private Map<ChatType, JMConversation> chatMap;

    @Autowired
    public JMConversationHelperImpl(List<JMConversation> conversations) {

        this.conversations = conversations;
    }

    @Override
    public void startNewChat(Client client) {

        endChat();

        this.client = client;
        chatMap = new HashMap<>();

        List<SocialProfile> socialProfiles = client.getSocialProfiles();

        //скидываем чаты
        conversations.forEach(JMConversation::dropChat);

        for (SocialProfile socialProfile : socialProfiles) {
            try {
                ChatType newChatType = ChatType.valueOf(socialProfile.getSocialProfileType().getName());

                JMConversation conversation = conversations.stream()
                        .filter(jmConversation -> jmConversation.getChatTypeOfConversation().equals(newChatType))
                        .findFirst().orElse(null);

                if (conversation != null) {
                    conversation.startNewChat(socialProfile.getLink());
                    chatMap.put(newChatType, conversation); //если null то просто нет чата.
                }
            } catch (NullPointerException e) {
                //нет чата для соц сети...
            }
        }
    }

    @Override
    public void endChat() {
        client = null;
        if (chatMap != null) {
            chatMap.clear();
        }
    }

    @Override
    public ChatMessage sendMessage(String text, ChatType chatType) {
        JMConversation conversation = chatMap.get(chatType);

        if (conversation == null) {
            return null;
        }

        return conversation.sendMessage(text);
    }

    @Override
    public List<ChatMessage> getNewMessages() {
        List<ChatMessage> list = new LinkedList<>();

        for (Map.Entry<ChatType, JMConversation> entity : chatMap.entrySet()) {
            List<ChatMessage> conversationMsg = entity.getValue().getNewMessages();
            list.addAll(0, conversationMsg);
        }

        list.sort(Comparator.comparing(ChatMessage::getTime));

        return list;
    }

    @Override
    public ChatMessage markMessageAsRead(ChatMessage message, ChatType chatType) {
        JMConversation conversation = chatMap.get(chatType);

        if (conversation == null) {
            return null;
        }

        return conversation.markMessageAsRead(message);
    }

    @Override
    public List<ChatMessage> getMessages() {
        List<ChatMessage> list = new LinkedList<>();

        for (Map.Entry<ChatType, JMConversation> entity : chatMap.entrySet()) {
            List<ChatMessage> conversationMsg = entity.getValue().getMessages(messageLimitInConversation);
            list.addAll(0, conversationMsg);
        }

        list.sort(Comparator.comparing(ChatMessage::getTime));

        return list;
    }
}
