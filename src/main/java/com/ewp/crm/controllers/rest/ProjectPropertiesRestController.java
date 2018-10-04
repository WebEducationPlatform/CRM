package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.ProjectProperties;
import com.ewp.crm.service.interfaces.MessageTemplateService;
import com.ewp.crm.service.interfaces.ProjectPropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;

@RestController
@RequestMapping("/rest/properties")
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
public class ProjectPropertiesRestController {

    private final ProjectPropertiesService projectPropertiesService;
    private final MessageTemplateService messageTemplateService;

    @Autowired
    public ProjectPropertiesRestController(ProjectPropertiesService projectPropertiesService, MessageTemplateService messageTemplateService) {
        this.projectPropertiesService = projectPropertiesService;
        this.messageTemplateService = messageTemplateService;
    }

    @GetMapping
    public ResponseEntity<ProjectProperties> getProjectProperties() {
        ProjectProperties projectProperties = projectPropertiesService.get();
        if (projectProperties == null) {
            projectProperties = new ProjectProperties();
        }
        return new ResponseEntity<>(projectProperties, HttpStatus.OK);
    }

    @PostMapping("/email-notification")
    public HttpStatus setPaymentNotificationSettings(@RequestParam( name = "paymentMessageTemplate") Long templateId,
                                                     @RequestParam( name = "paymentNotificationTime") String time,
                                                     @RequestParam( name = "paymentNotificationEnabled") Boolean enabled) {
        ProjectProperties current = projectPropertiesService.getOrCreate();
        if (templateId != null) {
            current.setPaymentMessageTemplate(messageTemplateService.get(templateId));
        } else {
            current.setPaymentMessageTemplate(null);
        }
        current.setPaymentNotificationTime(LocalTime.parse(time));
        current.setPaymentNotificationEnabled(enabled);
        projectPropertiesService.update(current);
        return HttpStatus.OK;
    }

    @GetMapping("/status")
    public ResponseEntity<Long> getStatus() {
        ProjectProperties pp = projectPropertiesService.get();
        Long status = -1L;
        if (pp != null) {
            status = pp.getDefaultStatusId();
        }
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
