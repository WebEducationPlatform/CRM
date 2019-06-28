package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.conversation.ChatMessage;
import com.ewp.crm.models.conversation.ChatType;
import com.ewp.crm.models.conversation.Interlocutor;
import com.ewp.crm.service.conversation.JMConversationHelper;
import com.ewp.crm.service.interfaces.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/rest/conversation")
public class JMConversationController {

    private final JMConversationHelper conversationHelper;
    private final ClientService clientService;

    @Autowired
    public JMConversationController(JMConversationHelper conversationHelper, ClientService clientService) {
        this.conversationHelper = conversationHelper;
        this.clientService = clientService;
    }

    @GetMapping(value = "/close", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'MENTOR')")
    public HttpEntity<?> closeChat(@RequestParam("id") long clientId) {
        conversationHelper.endChat(clientService.get(clientId));

        return ResponseEntity.EMPTY;
    }

    @PostMapping(value = "/send", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'MENTOR')")
    public ResponseEntity<ChatMessage> sendMessage(@RequestParam("text") String text,
                                                   @RequestParam("type") String chatType,
                                                   @RequestParam("chatId") String chatId) {
        ChatMessage chatMessage = conversationHelper.sendMessage(new ChatMessage(text, ChatType.valueOf(chatType), chatId));
        return ResponseEntity.ok(chatMessage);
    }

    @GetMapping(value = "/all-new", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'MENTOR')")
    public ResponseEntity<List<ChatMessage>> getNewMessages(@RequestParam("id") long clientId) {
        Optional<Client> client = clientService.getClientByID(clientId);
        return client.map(c -> ResponseEntity.ok(conversationHelper.getNewMessages(c))).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/last-read", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'MENTOR')")
    public ResponseEntity<Map<ChatType, String>> getLastReadMessageIds(@RequestParam("id") long clientId) {
        Optional<Client> client = clientService.getClientByID(clientId);
        return client.map(c -> new ResponseEntity<>(conversationHelper.getReadMessages(c), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'MENTOR')")
    public ResponseEntity<List<ChatMessage>> getAllMessage(@RequestParam("id") long clientId) {
        Optional<Client> client = clientService.getClientByID(clientId);
        if (client.isPresent()) {
            List<ChatMessage> messages = conversationHelper.getMessages(client.get());
            return ResponseEntity.ok(messages);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/interlocutors", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'MENTOR')")
    public ResponseEntity<List<Interlocutor>> getInterlocutors(@RequestParam("id") long clientId) {
        Optional<Client> client = clientService.getClientByID(clientId);
        return client.map(c -> ResponseEntity.ok(conversationHelper.getInterlocutors(c))).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/us", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'MENTOR')")
    public ResponseEntity<List<Interlocutor>> getUs() {
        return ResponseEntity.ok(conversationHelper.getUs());
    }

    @GetMapping(value = "/all-byClient", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'MENTOR')")
    public ResponseEntity<Map<Long, Integer>> getCountOfNewMessages() {
        return ResponseEntity.ok(conversationHelper.getCountOfNewMessages());
    }

}
