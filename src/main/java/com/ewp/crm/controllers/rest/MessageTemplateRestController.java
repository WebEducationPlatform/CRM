package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.MessageTemplate;
import com.ewp.crm.service.interfaces.MessageTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rest/message-template")
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
public class MessageTemplateRestController {

    private final MessageTemplateService messageTemplateService;

    @Autowired
    public MessageTemplateRestController(MessageTemplateService messageTemplateService) {
        this.messageTemplateService = messageTemplateService;
    }

    @GetMapping
    public ResponseEntity<List<MessageTemplate>> getAllMessageTemplates() {
        return new ResponseEntity<>(messageTemplateService.getAll(), HttpStatus.OK);
    }
}
