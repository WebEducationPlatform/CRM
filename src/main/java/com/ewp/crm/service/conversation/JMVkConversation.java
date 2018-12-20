package com.ewp.crm.service.conversation;

import com.ewp.crm.configs.VKConfigImpl;
import com.ewp.crm.configs.inteface.VKConfig;
import com.ewp.crm.exceptions.util.VKAccessTokenException;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.service.interfaces.SocialProfileService;
import com.ewp.crm.service.interfaces.VKService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class JMVkConversation implements JMConversation {

    //constant string
    private final String splitter = ",";
    private final String fldPhoto = "photo_50";
    private final String additionalGroup = "name,photo_50";
    private final String additionalUser = "photo_50,first_name_ins,last_name_ins";
    private final String defaultVkPhoto = "https://vk.com/images/camera_50.png?ava=1";
    private final String vkUrl = "https://vk.com/";
    private final String clubString = "club";
    private final String idString = "id";

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

        String link = null;
        for (SocialProfile sp : profiles) {
            if ("vk".equals(sp.getSocialProfileType().getName())) {
                link = sp.getLink();
            }
        }

        if (link == null) {
            return Optional.empty();
        }

        String id = vkService.getIdFromLink(link);

        if (id != null) {

            Map<String, String> param = vkService.getUserDataById(Long.parseLong(id), additionalUser, splitter);
            Interlocutor interlocutor = new Interlocutor(id, vkUrl + idString + id, param.get(fldPhoto), getChatTypeOfConversation());
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
            interlocutor = new Interlocutor(id, vkUrl + id, param.get(fldPhoto), getChatTypeOfConversation());

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
        Interlocutor interlocutor = getInterlocutor(client).get();
        return vkService.getMassagesFromGroup(interlocutor.getId(), count, false).orElse(new LinkedList<>());
    }

    public ChatMessage getLastMessages(String userid) {

        List<ChatMessage> chatMessages = vkService.getMassagesFromGroup(userid, 1, false).orElse(new LinkedList<>());

        if (chatMessages.isEmpty()) {
            return null;
        }

        return chatMessages.get(0);
    }

    @Override
    public String getReadMessages(Client client) {
        Interlocutor interlocutor = getInterlocutor(client).get();
        ChatMessage message = getLastMessages(interlocutor.getId());

        if (message == null) {
            return "";
        }

        return message.getId();
    }

    @Override
    public List<ChatMessage> getNewMessages(Client client, int count) {
        Interlocutor interlocutor = getInterlocutor(client).get();
        return vkService.getMassagesFromGroup(interlocutor.getId(), count, true).orElse(new LinkedList<>());
    }
}
