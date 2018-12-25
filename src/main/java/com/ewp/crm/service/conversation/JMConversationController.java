package com.ewp.crm.service.conversation;

import com.ewp.crm.models.Client;
import com.ewp.crm.service.interfaces.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
    public HttpEntity<?> closeChat(@RequestParam("id") long clientId) {
        conversationHelper.endChat(clientService.get(clientId));

        return ResponseEntity.EMPTY;
    }

    @PostMapping(value = "/send", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
    public ResponseEntity<ChatMessage> sendMessage(@RequestParam("text") String text,
                                                   @RequestParam("type") String chatType,
                                                   @RequestParam("chatId") String chatId) {
        ChatMessage chatMessage = conversationHelper.sendMessage(new ChatMessage(text, ChatType.valueOf(chatType), chatId));
        return ResponseEntity.ok(chatMessage);
    }

    @GetMapping(value = "/all-new", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
    public ResponseEntity<List<ChatMessage>> getNewMessages(@RequestParam("id") long clientId) {
        Client client = clientService.getClientByID(clientId);
        return ResponseEntity.ok(conversationHelper.getNewMessages(client));
    }

    @GetMapping(value = "/last-read", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
    public ResponseEntity<Map<ChatType, String>> getLastReadMessageIds(@RequestParam("id") long clientId) {
        Client client = clientService.getClientByID(clientId);
        return new ResponseEntity<>(conversationHelper.getReadMessages(client), HttpStatus.OK);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
    public ResponseEntity<List<ChatMessage>> getAllMessage(@RequestParam("id") long clientId) {
        Client client = clientService.getClientByID(clientId);
        List<ChatMessage> messages = conversationHelper.getMessages(client);
        return ResponseEntity.ok(messages);
    }

    @GetMapping(value = "/interlocutors", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
    public ResponseEntity<List<Interlocutor>> getInterlocutors(@RequestParam("id") long clientId) {
        Client client = clientService.getClientByID(clientId);
        return ResponseEntity.ok(conversationHelper.getInterlocutors(client));
    }

    @GetMapping(value = "/us", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
    public ResponseEntity<List<Interlocutor>> getUs(@RequestParam("id") long clientId) {
        return ResponseEntity.ok(conversationHelper.getUs());
    }

    @GetMapping(value = "/all-byClient", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
    public ResponseEntity<Map<Long, Integer>> getCountOfNewMessages() {
        return ResponseEntity.ok(conversationHelper.getCountOfNewMessages());
    }
}
