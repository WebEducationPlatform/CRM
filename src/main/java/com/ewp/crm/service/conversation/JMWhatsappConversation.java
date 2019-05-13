package com.ewp.crm.service.conversation;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.User;
import com.ewp.crm.models.conversation.ChatMessage;
import com.ewp.crm.models.conversation.ChatType;
import com.ewp.crm.models.conversation.Interlocutor;
import com.ewp.crm.models.whatsapp.WhatsappMessage;
import com.ewp.crm.models.whatsapp.whatsappDTO.WhatsappAcknowledgementDTO;
import com.ewp.crm.models.whatsapp.whatsappDTO.WhatsappCheckDeliveryMsg;
import com.ewp.crm.models.whatsapp.whatsappDTO.WhatsappMessageSendable;
import com.ewp.crm.repository.interfaces.ClientRepository;
import com.ewp.crm.service.interfaces.WhatsappMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;


@Component
@PropertySource("file:./whatsapp-chat-api.properties")
public class JMWhatsappConversation implements JMConversation {


    private static final Logger logger = LoggerFactory.getLogger(JMWhatsappConversation.class);
    private final ClientRepository clientRepository;
    private final Environment environment;
    private final WhatsappMessageService whatsappMessageService;

    @Autowired
    public JMWhatsappConversation(Environment environment, ClientRepository clientRepository, WhatsappMessageService whatsappMessageService) {
        this.clientRepository = clientRepository;
        this.environment = environment;
        this.whatsappMessageService = whatsappMessageService;
    }

    @Override
    public ChatType getChatTypeOfConversation() {
        return ChatType.whatsapp;
    }

    @Override
    public void endChat(Client client) {
        logger.info("чат с клиентом" + client.getName() + " " + client.getLastName() + "окончен");
    }

    @Override
    public ChatMessage sendMessage(ChatMessage message) {

        String phoneNumber;

        try {
            phoneNumber = message.getChatId().replaceAll("\\D", "");
        } catch (NumberFormatException nfe) {
            logger.warn("При отправке сообщения в WhatsApp был указан не верный номер телефона в поле ChatId :", nfe.getMessage());
            return new ChatMessage(ChatType.whatsapp, environment.getProperty("messaging.whatsapp.send-error-wrong-phone"));
        }

        WhatsappMessageSendable whatsappMessageSendable = new WhatsappMessageSendable(phoneNumber, message.getText());

        String sendUrl = "https://" + environment.getProperty("server.api") + ".chat-api.com/" + environment.getProperty("instance") + "/sendMessage?" +
                environment.getProperty("token");


        new RestTemplate().postForObject(sendUrl, whatsappMessageSendable, WhatsappCheckDeliveryMsg.class);
        Optional<WhatsappMessage> lastMessage = whatsappMessageService.findTopByFromMeTrueOrderByMessageNumberDesc();

        String lastMessagesUrl = "https://" + environment.getProperty("server.api") +
                ".chat-api.com/" + environment.getProperty("instance") +
                "/messages?" + environment.getProperty("token") + "&lastMessageNumber=" + (!lastMessage.isPresent()?0+"&last":lastMessage.get().getMessageNumber() - 2);
        boolean messgesUpdated =false;
        Long lastMessageNumber;
        String id;
        while (true){

            WhatsappAcknowledgementDTO lastMessagesFromApi = new RestTemplate().getForObject(lastMessagesUrl, WhatsappAcknowledgementDTO.class);
            Optional<WhatsappMessage> any = lastMessagesFromApi.getMessages().stream().filter(x -> x.getChatId().contains(phoneNumber) && x.getBody().equals(message.getText())).findAny();
            id = any.map(WhatsappMessage::getId).orElse(null);
            lastMessageNumber = any.map(WhatsappMessage::getMessageNumber).orElse(null);
            if (any.isPresent()){
                break;
            }
        }


        WhatsappMessage whatsappMessage =
                new WhatsappMessage(message.getText(), true, ZonedDateTime.now(ZoneId.systemDefault()), message.getChatId(), lastMessageNumber,
                        SecurityContextHolder.getContext().getAuthentication().getName(), clientRepository.getClientByPhoneNumber(message.getChatId()));


        whatsappMessage.setId(id);
        message.setId(String.valueOf(lastMessageNumber));

        whatsappMessageService.save(whatsappMessage);

        return message;
    }

    @Override
    public Map<Client, Integer> getCountOfNewMessages() {

        Map<Client, Integer> clientNotSeenMsgCount = new HashMap<>();
        List<WhatsappMessage> whatsappMessages = whatsappMessageService.findAllBySeenFalseAndFromMeFalse();

        for (WhatsappMessage message : whatsappMessages) {

            Client client = message.getClient();
            List<WhatsappMessage> whatsappMessagesForClient = client.getWhatsappMessages();

            int counter = 0;

            for (WhatsappMessage wm : whatsappMessagesForClient) {
                if (!wm.isSeen()) {
                    counter++;
                }
            }

            clientNotSeenMsgCount.put(client, counter);

        }
        return clientNotSeenMsgCount;
    }

    @Override
    public List<ChatMessage> getNewMessages(Client client, int count) {
        List<WhatsappMessage> allByIsSeen = whatsappMessageService.findTop40BySeenFalseAndClient_IdOrderByTimeDesc(client.getId());
        setSeenMsgToMe(allByIsSeen);
        return whatsappMsgToChatMsg(allByIsSeen);
    }

    private List<ChatMessage> whatsappMsgToChatMsg(List<WhatsappMessage> allByIsRead) {

        List<ChatMessage> chatMessages = new ArrayList<>();
        for (WhatsappMessage wm : allByIsRead) {
            chatMessages.add(new ChatMessage(String.valueOf(wm.getMessageNumber()), wm.getChatId(), ChatType.whatsapp, wm.getBody(), wm.getTime(), wm.isSeen(), wm.isFromMe()));
        }
        return chatMessages;
    }

    @Override
    public List<ChatMessage> getMessages(Client client, int count) {
        List<WhatsappMessage> all = whatsappMessageService.findAllByClient_Id(client.getId());
        setSeenMsgToMe(all);
        return whatsappMsgToChatMsg(all);
    }

    private void setSeenMsgToMe(List<WhatsappMessage> all) {
        for (WhatsappMessage whatsappMessage : all) {
            if (!whatsappMessage.isFromMe()) {
                whatsappMessage.setSeen(true);
            }
        }
        whatsappMessageService.saveAll(all);
    }

    @Override
    public String getReadMessages(Client client) {
        Optional<WhatsappMessage> lastSeenFromMeWhatsappMessage = whatsappMessageService.findTopByClient_IdAndSeenTrueAndFromMeTrueOrderByTimeDesc(client.getId());
        return lastSeenFromMeWhatsappMessage.map(whatsappMessage -> String.valueOf(whatsappMessage.getMessageNumber())).orElse("");
    }

    @Override
    public Optional<Interlocutor> getInterlocutor(Client client) {
        Optional<String> phoneOptional = client.getPhoneNumber();
        if (phoneOptional.isPresent() && !phoneOptional.get().isEmpty()) {
            return Optional.of(new Interlocutor(client.getPhoneNumber().get(), "#", "#", client.getName(), ChatType.whatsapp));
        } else return Optional.empty();
    }

    @Override
    public Optional<Interlocutor> getMe() {
        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Optional.of(new Interlocutor(u.getPhoneNumber(), "#", "#", u.getFirstName(), ChatType.whatsapp));

    }
}
