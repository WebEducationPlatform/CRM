package com.ewp.crm.service.conversation;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.models.conversation.ChatMessage;
import com.ewp.crm.models.conversation.ChatType;
import com.ewp.crm.models.conversation.Interlocutor;
import com.ewp.crm.service.interfaces.SlackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JMSlackConversation implements JMConversation {

    private static final Logger logger = LoggerFactory.getLogger(JMSlackConversation.class);

    private final SlackService slackService;
    private Environment env;

    @Autowired
    public JMSlackConversation(SlackService slackService, Environment env) {
        this.slackService = slackService;
        this.env = env;
    }

    @Override
    public ChatType getChatTypeOfConversation() {
        return ChatType.slack;
    }

    @Override
    public void endChat(Client client) {
        logger.info("Slack чат с клиентом" + client.getName() + " " + client.getLastName() + "окончен");
    }

    @Override
    public ChatMessage sendMessage(ChatMessage message) {
        boolean isSent = slackService.trySendMessageToSlackUser(message.getChatId(), message.getText());
        return isSent ? message : new ChatMessage(ChatType.slack, env.getProperty("messaging.slack.send-error"));
    }

    @Override
    public Map<Client, Integer> getCountOfNewMessages() {
        return new HashMap<>();
    }

    @Override
    public List<ChatMessage> getNewMessages(Client client, int count) {
        return new ArrayList<>();
    }

    @Override
    public List<ChatMessage> getMessages(Client client, int count) {
        return new ArrayList<>();
    }

    @Override
    public String getReadMessages(Client client) {
        return "";
    }

    @Override
    public Optional<Interlocutor> getInterlocutor(Client client) {
        Optional<SocialProfile> clientSlackProfile = client
                .getSocialProfiles()
                .stream()
                .filter(profile -> "slack".equals(profile.getSocialNetworkType().getName()))
                .findFirst();
        return clientSlackProfile.map(socialProfile -> new Interlocutor(
                socialProfile.getSocialId(),
                "#",
                "#",
                client.getName(),
                ChatType.slack));
    }

    @Override
    public Optional<Interlocutor> getMe() {
        // Просто заглушка, т.к. сообщения Slack только отправляются, но не принимаются
        return Optional.of(new Interlocutor("11111", "#", "#", "USERNAME", ChatType.slack));
    }
}
