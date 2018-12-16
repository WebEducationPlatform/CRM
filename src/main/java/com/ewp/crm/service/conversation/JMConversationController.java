package com.ewp.crm.service.conversation;

import com.ewp.crm.models.Client;
import com.ewp.crm.service.interfaces.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
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
    public ResponseEntity<ChatMessage> sendMessage(@RequestParam String text,
                                                   @RequestParam String chatType) {
        ChatMessage chatMessage = conversationHelper.sendMessage(new ChatMessage(ChatType.valueOf(chatType), text));
        return ResponseEntity.ok(chatMessage);
    }

    @GetMapping(value = "/all-new", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
    public ResponseEntity<Map<String, List<ChatMessage>>> getNewMessages(@RequestParam("id") long clientId) {
        Map<String, List<ChatMessage>> response = new HashMap<>();
        Client client = clientService.getClientByID(clientId);
        List<ChatMessage> newMessages = conversationHelper.getNewMessages(client);
        response.put("new", newMessages);
        List<ChatMessage> readMessages = conversationHelper.getReadMessages(client);
        response.put("read", readMessages);
        return ResponseEntity.ok(response);
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
        Client client = clientService.getClientByID(clientId);
        return ResponseEntity.ok(conversationHelper.getUs(client));
    }
}
