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
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'HR')")
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
        if (messageTemplateService.getByName(name).isPresent()) {
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

    @PostMapping ("/rename")
    public HttpStatus renameTemplate(@RequestParam("id") Long id, @RequestParam("name") String name) {
        if(messageTemplateService.get(id) != null) {
            if (!messageTemplateService.getByName(name).isPresent()) {
                MessageTemplate template = messageTemplateService.get(id);
                template.setName(name);
                messageTemplateService.update(template);
                return HttpStatus.OK;
            } else {
                logger.info("Template with name {} already exists", name);
                return HttpStatus.BAD_REQUEST;
            }
        } else {
            logger.info("Template with id {} doesn't exist", id);
            return HttpStatus.BAD_REQUEST;
        }
    }

    @PostMapping ("/renameTheme")
    public HttpStatus renameThemeTemplate(@RequestParam("id") Long id, @RequestParam("theme") String theme) {
        MessageTemplate messageTemplate = messageTemplateService.get(id);
        if(messageTemplate != null) {
           messageTemplate.setTheme(theme);
           messageTemplateService.update(messageTemplate);
        } else {
            logger.info("Template with id {} doesn't exist", id);
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.OK;
    }
}
