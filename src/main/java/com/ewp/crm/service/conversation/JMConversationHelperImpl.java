package com.ewp.crm.service.conversation;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.conversation.ChatMessage;
import com.ewp.crm.models.conversation.ChatType;
import com.ewp.crm.models.conversation.Interlocutor;
import com.ewp.crm.service.impl.TelegramServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JMConversationHelperImpl implements JMConversationHelper {

    private final int CHAT_MESSAGE_LIMIT = 40;

    private final List<JMConversation> conversations;
    private static Logger logger = LoggerFactory.getLogger(JMConversationHelperImpl.class);

    @Autowired
    public JMConversationHelperImpl(List<JMConversation> conversations) {
        List<JMConversation> result = new ArrayList<>();
        for (JMConversation conversation : conversations) {
            if (conversation instanceof  TelegramServiceImpl) {
                if (!((TelegramServiceImpl) conversation).isTdlibInstalled()) {
                    logger.error("Telegram conversations not available. Tdlib not installed correctly!");
                    continue;
                }
            }
//            if (conversation instanceof JMVkConversation)
//                continue;
            result.add(conversation);

        }
        this.conversations = result;
    }

    @Override
    public void endChat(Client client) {
        for (JMConversation conversation: conversations) {
            conversation.endChat(client);
        }
    }

    @Override
    public ChatMessage sendMessage(ChatMessage message) {
        for (JMConversation conversation: conversations) {
            if (message.getChatType() == conversation.getChatTypeOfConversation()) {
                return conversation.sendMessage(message);
            }
        }
        return message;
    }

    @Override
    public Map<Long, Integer> getCountOfNewMessages() {
        Map<Long, Integer> clientMap = new HashMap<>();
        for (JMConversation conversation: conversations) {
            Map<Client, Integer> newMessageFromConversation = conversation.getCountOfNewMessages();
            for(Map.Entry<Client, Integer> element: newMessageFromConversation.entrySet()){
                Integer count = clientMap.getOrDefault(element.getKey().getId(), 0);
                clientMap.put(element.getKey().getId(), element.getValue()+count);
            }
        }
        return clientMap;
    }

    @Override
    public List<ChatMessage> getNewMessages(Client client) {
        List<ChatMessage> list = new LinkedList<>();
        for (JMConversation conversation: conversations) {
            List<ChatMessage> conversationMsg = conversation.getNewMessages(client, CHAT_MESSAGE_LIMIT);
            if (conversationMsg != null && !conversationMsg.isEmpty()) {
                list.addAll(0, conversationMsg);
            }
        }
        list.sort(Comparator.comparing(ChatMessage::getTime));
        return list;
    }

    @Override
    public List<ChatMessage> getMessages(Client client) {
        List<ChatMessage> list = new LinkedList<>();
        for (JMConversation conversation: conversations) {
            List<ChatMessage> conversationMsg = conversation.getMessages(client, CHAT_MESSAGE_LIMIT);
            if (conversationMsg != null && !conversationMsg.isEmpty()) {
                list.addAll(conversationMsg);
            }
        }
        list.sort(Comparator.comparing(ChatMessage::getTime));
        if (list.size() > 40) {
            list = list.subList(list.size() - 40, list.size());
        }
        return list;
    }

    @Override
    public Map<ChatType, String> getReadMessages(Client client) {
        Map<ChatType, String> chatTypeStringMap = new HashMap<>();
        for (JMConversation conversation: conversations) {
            String lastMsg = conversation.getReadMessages(client);
            chatTypeStringMap.put(conversation.getChatTypeOfConversation(), lastMsg);
        }
        return chatTypeStringMap;
    }

    @Override
    public List<Interlocutor> getInterlocutors(Client client) {
        List<Interlocutor> list = new LinkedList<>();
        for (JMConversation conversation: conversations) {
            Optional<Interlocutor> interlocutor = conversation.getInterlocutor(client);
            interlocutor.ifPresent(list::add);
        }
        return list;
    }

    @Override
    public List<Interlocutor> getUs() {
        List<Interlocutor> list = new LinkedList<>();
        for (JMConversation conversation: conversations) {
            Optional<Interlocutor> interlocutor = conversation.getMe();
            interlocutor.ifPresent(list::add);
        }
        return list;
    }

}
