package com.ewp.crm.service.conversation;

import com.ewp.crm.configs.VKConfigImpl;
import com.ewp.crm.configs.inteface.VKConfig;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.models.conversation.ChatMessage;
import com.ewp.crm.models.conversation.ChatType;
import com.ewp.crm.models.conversation.Interlocutor;
import com.ewp.crm.service.interfaces.SocialProfileService;
import com.ewp.crm.service.interfaces.VKService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JMVkConversation implements JMConversation {

    //constant string
    private final int MAX_MESSAGE_IN_QUEUE = 200;
    private final String splitter          = ",";
    private final String fldPhoto          = "photo_50";
    private final String fldFirstName      = "first_name";
    private final String fldLastName       = "last_name";
    private final String fldName           = "name";
    private final String additionalGroup   = "name,photo_50";
    private final String additionalUser    = "first_name,last_name,photo_50,first_name_ins,last_name_ins";
    private final String defaultVkPhoto    = "https://vk.com/images/camera_50.png?ava=1";
    private final String vkUrl             = "https://vk.com/";
    private final String clubString        = "club";
    private final String idString          = "id";

    private final VKConfig vkConfig;
    private final VKService vkService;
    private final SocialProfileService socialProfileService;

    @Autowired
    public JMVkConversation(VKConfigImpl vkConfig, VKService vkService, SocialProfileService socialProfileService) {
        this.vkConfig = vkConfig;
        this.vkService = vkService;
        this.socialProfileService = socialProfileService;
    }

    @Override
    public void endChat(Client client) {

    }

    @Override
    public ChatType getChatTypeOfConversation() {
        return ChatType.vk;
    }

    @Override
    public Optional<Interlocutor> getInterlocutor(Client client) {
        List<SocialProfile> profiles = client.getSocialProfiles();

        String id = null;
        for (SocialProfile sp : profiles) {
            if ("vk".equals(sp.getSocialNetworkType().getName())) {
                id = sp.getSocialId();
            }
        }

        if (id != null) {
            Map<String, String> param = vkService.getUserDataById(Long.parseLong(id), additionalUser, splitter);
            Interlocutor interlocutor = new Interlocutor(id, vkUrl + idString + id,
                                                        param.get(fldPhoto),
                                             param.get(fldFirstName) + " " + param.get(fldLastName),
                                                        getChatTypeOfConversation());
            return Optional.of(interlocutor);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Interlocutor> getMe() {

        Interlocutor interlocutor;
        try {
            String id = vkConfig.getClubId();

            Map<String, String> param = vkService.getGroupDataById(Long.parseLong(id), additionalGroup, splitter);
            interlocutor = new Interlocutor(id, vkUrl + id, param.get(fldPhoto), param.get(fldName), getChatTypeOfConversation());

        } catch (Exception e) {
            return Optional.empty();
        }
        return Optional.of(interlocutor);
    }

    @Override
    public ChatMessage sendMessage(ChatMessage message) {
        vkService.sendMessageById(Long.parseLong(message.getChatId()), message.getText(),vkConfig.getCommunityToken());
        return getLastMessages(message.getChatId());
    }

    @Override
    public List<ChatMessage> getMessages(Client client, int count) {

        Optional<Interlocutor> interlocutor = getInterlocutor(client);
        List<ChatMessage> chatMessages;

        if (interlocutor.isPresent()){

            Optional<List<ChatMessage>> vkChatMessages = vkService.getMassagesFromGroup(interlocutor.get().getId(), count, false, false);

            if (vkChatMessages.isPresent()) {
                chatMessages = vkChatMessages.get();
                markAsRead(interlocutor, chatMessages);
                return chatMessages;
            }

        }

        return new LinkedList<>();
    }

    @Override
    public Map<Client, Integer> getCountOfNewMessages() {
        return vkService.getNewMassagesFromGroup().orElse(new HashMap<>());
    }

    public ChatMessage getLastReadMessages(String userid) {

        List<ChatMessage> chatMessages = vkService.getMassagesFromGroup(userid, MAX_MESSAGE_IN_QUEUE, true, false).orElse(new LinkedList<>());

        if (chatMessages.isEmpty()) {
            return null;
        }

        return chatMessages.get(0);
    }

    public ChatMessage getLastMessages(String userid) {

        List<ChatMessage> chatMessages = vkService.getMassagesFromGroup(userid, 1, false, false).orElse(new LinkedList<>());

        if (chatMessages.isEmpty()) {
            return null;
        }

        return chatMessages.get(0);
    }
    @Override
    public String getReadMessages(Client client) {
        Optional<Interlocutor> interlocutor = getInterlocutor(client);

        ChatMessage message = null;

        if (interlocutor.isPresent()){
            message = getLastReadMessages(interlocutor.get().getId());
        }

        if (message == null) {
            return "";
        }

        return message.getId();
    }

    @Override
    public List<ChatMessage> getNewMessages(Client client, int count) {
        Optional<Interlocutor> interlocutor = getInterlocutor(client);

        List<ChatMessage> chatMessages = new LinkedList<>();

        if (interlocutor.isPresent()) {

            Optional<List<ChatMessage>> vkChatMessages = vkService.getMassagesFromGroup(interlocutor.get().getId(), count, false, true);

            if (vkChatMessages.isPresent()) {
                chatMessages = vkChatMessages.get();
                markAsRead(interlocutor, chatMessages);
                return chatMessages;
            }
        }
        return chatMessages;
    }

    //Помечаем сообщения как прочитанные
    private void markAsRead(Optional<Interlocutor> interlocutor, List<ChatMessage> chatMessages) {
        for (ChatMessage chatMessage : chatMessages) {
            vkService.markAsRead(interlocutor.get().getId(), vkConfig.getCommunityToken(), chatMessage.getId());
            chatMessage.setRead(true);
        }
    }
}
