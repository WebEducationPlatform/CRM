package com.ewp.crm.service.conversation;

import com.ewp.crm.models.Client;
import com.ewp.crm.service.interfaces.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping(value = "/init{clientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
    public HttpEntity<?> initChat(@PathVariable String clientId) {

        Client client = clientService.get(Long.parseLong(clientId));
        conversationHelper.startNewChat(client);

        return ResponseEntity.EMPTY;
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

    @PostMapping(value = "/mark", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
    public ResponseEntity<ChatMessage> markAsReadMessage(@RequestBody ChatMessage message,
                                                         @RequestParam String chatType) {

        ChatMessage chatMessage = conversationHelper.markMessageAsRead(message, ChatType.valueOf(chatType));

        return ResponseEntity.ok(chatMessage);
    }

    @GetMapping(value = "/allNew", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
    public ResponseEntity<List<ChatMessage>> getAllNewMessage() {

        List<ChatMessage> messages = conversationHelper.getNewMessages();
        return ResponseEntity.ok(messages);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
    public ResponseEntity<List<ChatMessage>> getAllMessage() {

        List<ChatMessage> messages = conversationHelper.getMessages();
        return ResponseEntity.ok(messages);
    }
}
