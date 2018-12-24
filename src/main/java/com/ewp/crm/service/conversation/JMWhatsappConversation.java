package com.ewp.crm.service.conversation;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.whatsapp.WhatsappMessage;
import com.ewp.crm.models.whatsapp.whatsappDTO.WhatsappCheckDeliveryMsg;
import com.ewp.crm.models.whatsapp.whatsappDTO.WhatsappMessageSendable;
import com.ewp.crm.repository.interfaces.ClientRepository;
import com.ewp.crm.repository.interfaces.WhatsappMessageRepository;
import org.apache.commons.lang3.RandomStringUtils;
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

    private final String sendUrl;
    private static final Logger logger = LoggerFactory.getLogger(JMWhatsappConversation.class);
    private final ClientRepository clientRepository;
    private final WhatsappMessageRepository whatsappMessageRepository;

    @Autowired
    public JMWhatsappConversation(Environment environment, ClientRepository clientRepository, WhatsappMessageRepository whatsappMessageRepository) {
        this.sendUrl = "https://" + environment.getProperty("server.api") +
                ".chat-api.com/" + environment.getProperty("instance") +
                "/sendMessage?" + environment.getProperty("token");
        this.clientRepository = clientRepository;
        this.whatsappMessageRepository = whatsappMessageRepository;
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
        long number;
        try {
            number = Long.parseLong(message.getChatId().replaceAll("\\D", ""));
        } catch (NumberFormatException nfe) {
            logger.warn("При отправки сообщения в WhatsApp был указан не верный номер телефона в поле ChatId :", nfe.getMessage());
            return new ChatMessage(ChatType.whatsapp, "у этого клиента не верно указан номер телефона оправка сообщения не возможна");
        }

        WhatsappMessageSendable whatsappMessageSendable = new WhatsappMessageSendable(number, message.getText());
        WhatsappCheckDeliveryMsg whatsappCheckDeliveryMsg = new RestTemplate().postForObject(sendUrl, whatsappMessageSendable, WhatsappCheckDeliveryMsg.class);
        WhatsappMessage whatsappMessage = new WhatsappMessage(RandomStringUtils.random(3)+System.currentTimeMillis(),message.getText(), true, ZonedDateTime.now(ZoneId.systemDefault()), message.getChatId(), whatsappCheckDeliveryMsg.getQueueNumber(),
                SecurityContextHolder.getContext().getAuthentication().getName(), clientRepository.getClientByPhoneNumber(message.getChatId()));
        whatsappMessageRepository.save(whatsappMessage);
        return message;
    }

    @Override
    public Map<Client, Integer> getCountOfNewMessages() {
        return null;
    }

    @Override
    public List<ChatMessage> getNewMessages(Client client, int count) {
        List<WhatsappMessage> allByIsRead = whatsappMessageRepository.findAllByisRead(false);
               return whatsappMsgToChatMsg(allByIsRead);
    }

    private List<ChatMessage> whatsappMsgToChatMsg(List<WhatsappMessage> allByIsRead) {
        List<ChatMessage> chatMessages = new ArrayList<>();
        for (WhatsappMessage wm : allByIsRead) {
            chatMessages.add(new ChatMessage(wm.getId(), wm.getChatId(), ChatType.whatsapp, wm.getBody(), wm.getTime(), wm.isRead(), false));
        }
        return chatMessages;
    }

    @Override
    public List<ChatMessage> getMessages(Client client, int count) {
        List<WhatsappMessage> all = whatsappMessageRepository.findAll();
        return whatsappMsgToChatMsg(all);
    }

    @Override
    public String getReadMessages(Client client) {
//        List<WhatsappMessage> allByIsRead = whatsappMessageRepository.findAllByisRead(false);
        return "";
//                whatsappMsgToChatMsg(allByIsRead).toString();
        // Почему сдесь строка?????????????
    }

    @Override
    public Optional<Interlocutor> getInterlocutor(Client client) {
        return Optional.empty();
    }

    @Override
    public Optional<Interlocutor> getMe() {
        return Optional.empty();
    }
}
