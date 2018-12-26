package com.ewp.crm.service.conversation;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.User;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

        long phoneNumber;

        try {
            phoneNumber = Long.parseLong(message.getChatId().replaceAll("\\D", ""));
        } catch (NumberFormatException nfe) {
            logger.warn("При отправки сообщения в WhatsApp был указан не верный номер телефона в поле ChatId :", nfe.getMessage());
            return new ChatMessage(ChatType.whatsapp, "у этого клиента не верно указан номер телефона оправка сообщения не возможна");
        }

        WhatsappMessageSendable whatsappMessageSendable = new WhatsappMessageSendable(phoneNumber, message.getText());

        String sendUrl = "https://" + environment.getProperty("server.api") + ".chat-api.com/" + environment.getProperty("instance") + "/sendMessage?" +
                environment.getProperty("token");


        WhatsappCheckDeliveryMsg whatsappCheckDeliveryMsg = new RestTemplate().postForObject(sendUrl, whatsappMessageSendable, WhatsappCheckDeliveryMsg.class);

        WhatsappMessage whatsappMessage =
                new WhatsappMessage(message.getText(), true, ZonedDateTime.now(ZoneId.systemDefault()), message.getChatId(), whatsappCheckDeliveryMsg.getQueueNumber(),
                        SecurityContextHolder.getContext().getAuthentication().getName(), clientRepository.getClientByPhoneNumber(message.getChatId()));

        whatsappMessageService.save(whatsappMessage);

        String lastMessagesUrl = "https://" + environment.getProperty("server.api") +
                ".chat-api.com/" + environment.getProperty("instance") +
                "/messages?" + environment.getProperty("token") + "&lastMessageNumber=" + (whatsappMessage.getMessageNumber() - 1);

        WhatsappAcknowledgementDTO forObject = new RestTemplate().getForObject(lastMessagesUrl, WhatsappAcknowledgementDTO.class);

        Optional<WhatsappMessage> any = forObject.getMessages().stream().filter(x -> x.getMessageNumber() == whatsappMessage.getMessageNumber()).findAny();
        String id = any.map(WhatsappMessage::getId).orElse(null);
        message.setId(id);

        return message;
    }

    @Override
    public Map<Client, Integer> getCountOfNewMessages() {

        Map<Client, Integer> clientNotSeenMsgCount = new HashMap<>();
        List<WhatsappMessage> whatsappMessages = whatsappMessageService.findAllBySeenFalse();

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
        return whatsappMsgToChatMsg(allByIsSeen);
    }

    private List<ChatMessage> whatsappMsgToChatMsg(List<WhatsappMessage> allByIsRead) {

        List<ChatMessage> chatMessages = new ArrayList<>();

        for (WhatsappMessage wm : allByIsRead) {
            chatMessages.add(new ChatMessage(wm.getId(), wm.getChatId(), ChatType.whatsapp, wm.getBody(), wm.getTime(), wm.isSeen(), wm.isFromMe()));
        }

        return chatMessages;
    }

    @Override
    public List<ChatMessage> getMessages(Client client, int count) {
        List<WhatsappMessage> all = whatsappMessageService.findAllByClient_Id(client.getId());
        return whatsappMsgToChatMsg(all);
    }

    @Override
    public String getReadMessages(Client client) {
        return "";
        //TODO Null pointer тут.
//        return whatsappMessageService.findTopByClient_IdOrderByTimeDesc(client.getId()).getId();
    }

    @Override
    public Optional<Interlocutor> getInterlocutor(Client client) {
        return Optional.of(new Interlocutor(client.getPhoneNumber(), "", "", "", ChatType.whatsapp));
    }

    @Override
    public Optional<Interlocutor> getMe() {
        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Optional.of(new Interlocutor(u.getPhoneNumber(), "", "", "", ChatType.whatsapp));

    }
}
