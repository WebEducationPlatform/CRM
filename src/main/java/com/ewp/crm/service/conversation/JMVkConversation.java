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

@Component
public class JMVkConversation implements JMConversation {

    //constant string
    private final String splitter        = ",";
    private final String fldPhoto        = "photo_50";
    private final String additionalGroup = "name,photo_50";
    private final String additionalUser  = "photo_50,first_name_ins, last_name_ins";
    private final String defaultVkPhoto  = "https://vk.com/images/camera_50.png?ava=1";
    private final String vkUrl           = "https://vk.com/";
    private final String clubString      = "club";
    private final String idString        = "id";

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
    public Interlocutor getInterlocutor(Client client) {
        List<SocialProfile> profiles = client.getSocialProfiles();

        String link = null;
        for(SocialProfile sp: profiles){
            if ("vk".equals(sp.getSocialProfileType().getName())){
                link = sp.getLink();
            }
        }

        if (link == null){
            return null;
        }

        String id = vkService.getIdFromLink(link);

        if (id != null){

            Map<String, String> param = vkService.getUserDataById(Long.parseLong(id), additionalUser, splitter);
            Interlocutor interlocutor = new Interlocutor(id, vkUrl+idString+id, param.get(fldPhoto), getChatTypeOfConversation());
            return interlocutor;

        }

        return null;
    }

    @Override
    public Interlocutor getMe() {
        String id = vkConfig.getClubId();

        Map<String, String> param = vkService.getGroupDataById(Long.parseLong(id), additionalGroup, splitter);
        Interlocutor interlocutor = new Interlocutor(id, vkUrl+id, param.get(fldPhoto), getChatTypeOfConversation());

        return interlocutor;
    }

    @Override
    public ChatMessage sendMessage(ChatMessage message) {
        vkService.sendMessageById(Long.parseLong(message.getChatId()), message.getText());
        message.setOutgoing(true);
        return message;
    }

    @Override
    public List<ChatMessage> getMessages(String chatId, int count) {

        try {
            return vkService.getMassagesFromGroup(chatId, count).orElse(new LinkedList<>());
        }
        catch (VKAccessTokenException e){
            return new LinkedList<>();
        }
    }

    @Override
    public String getReadMessages(Client client) {
//        try {
//            return vkService.getMassagesFromGroup(chatId, count).orElse(new LinkedList<>());
//        }
//        catch (VKAccessTokenException e){
            return "0";
        //}
    }

    @Override
    public List<ChatMessage> getNewMessages(String chatId, int count) {
        try {
            return vkService.getNewMassagesFromGroup(chatId).orElse(new LinkedList<>());
        }
        catch (VKAccessTokenException e){
            return new LinkedList<>();
        }
    }
}
