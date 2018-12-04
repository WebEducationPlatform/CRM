package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.MessageTemplate;
import com.ewp.crm.service.interfaces.MessageTemplateService;
import com.ewp.crm.service.interfaces.ProjectPropertiesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/message-template")
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
public class MessageTemplateRestController {

    private static Logger logger = LoggerFactory.getLogger(ClientRestController.class);

    private final MessageTemplateService messageTemplateService;
    private final ProjectPropertiesService projectPropertiesService;

    @Autowired
    public MessageTemplateRestController(MessageTemplateService messageTemplateService, ProjectPropertiesService projectPropertiesService) {
        this.messageTemplateService = messageTemplateService;
        this.projectPropertiesService = projectPropertiesService;
    }

    @GetMapping
    public ResponseEntity<List<MessageTemplate>> getAllMessageTemplates() {
        return new ResponseEntity<>(messageTemplateService.getAll(), HttpStatus.OK);
    }

    @PostMapping
    public HttpStatus createTemplate(@RequestParam("name") String name) {
        HttpStatus status = HttpStatus.OK;
        if (messageTemplateService.getByName(name) == null) {
            messageTemplateService.add(new MessageTemplate(name,"%bodyText%","%bodyText%g"));
            logger.info("Template with name {} created", name);
        } else {
            logger.info("Template with name {} already exists", name);
            status = HttpStatus.CONFLICT;
        }
        return status;
    }

    @PostMapping ("/delete")
    public HttpStatus deleteTemplate(@RequestParam("id") Long id) {
        HttpStatus result = HttpStatus.OK;
        MessageTemplate notificationTemplate = projectPropertiesService.getOrCreate().getPaymentMessageTemplate();
        if (notificationTemplate != null && id.equals(notificationTemplate.getId())) {
            result = HttpStatus.CONFLICT;
            logger.info("Template with id {} is used by payment notification", id);
        } else {
            messageTemplateService.delete(id);
            logger.info("Template with id {} deleted", id);
        }
        return result;
    }

}
