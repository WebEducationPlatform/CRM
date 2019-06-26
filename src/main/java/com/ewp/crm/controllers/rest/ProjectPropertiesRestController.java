package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.ProjectProperties;
import com.ewp.crm.service.interfaces.MessageTemplateService;
import com.ewp.crm.service.interfaces.ProjectPropertiesService;
import com.ewp.crm.service.interfaces.StudentStatusService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalTime;

@RestController
@RequestMapping("/rest/properties")
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'MENTOR')")
public class ProjectPropertiesRestController {

    private final ProjectPropertiesService projectPropertiesService;
    private final MessageTemplateService messageTemplateService;
    private final StudentStatusService studentStatusService;

    @Autowired
    public ProjectPropertiesRestController(ProjectPropertiesService projectPropertiesService,
                                           MessageTemplateService messageTemplateService,
                                           StudentStatusService studentStatusService) {
        this.projectPropertiesService = projectPropertiesService;
        this.messageTemplateService = messageTemplateService;
        this.studentStatusService = studentStatusService;
    }

    @GetMapping
    public ResponseEntity<ProjectProperties> getProjectProperties() {
        return new ResponseEntity<>(projectPropertiesService.getOrCreate(), HttpStatus.OK);
    }

    @PostMapping("/notifications")
    public HttpStatus setPaymentNotificationSettings(@RequestParam(name = "paymentMessageTemplate") Long templateId,
                                                     @RequestParam(name = "paymentNotificationTime") String time,
                                                     @RequestParam(name = "paymentNotificationEnabled") Boolean enabled,
                                                     @RequestParam(name = "newClientMessageTemplate") Long newClientTemplateId) {
        ProjectProperties current = projectPropertiesService.getOrCreate();
        if (templateId == null) {
            current.setPaymentMessageTemplate(null);
        } else {
            current.setPaymentMessageTemplate(messageTemplateService.get(templateId));
        }
        current.setPaymentNotificationTime(LocalTime.parse(time));
        current.setPaymentNotificationEnabled(enabled);
        if (newClientTemplateId == null) {
            current.setNewClientMessageTemplate(null);
        } else {
            current.setNewClientMessageTemplate(messageTemplateService.get(newClientTemplateId));
        }
        projectPropertiesService.update(current);
        return HttpStatus.OK;
    }

    @GetMapping("/get-slack-users")
    public ResponseEntity getSlackDefaultUsers() {
        ProjectProperties current = projectPropertiesService.getOrCreate();
        return ResponseEntity.ok(current.getSlackDefaultUsers());
    }

    @GetMapping("/get-slack-link")
    public ResponseEntity getSlackInviteLink() {
        ProjectProperties current = projectPropertiesService.getOrCreate();
        return ResponseEntity.ok(current.getSlackInviteLink());
    }

    @PostMapping("/slack-set")
    public ResponseEntity setSlackDefaultUsers(@RequestParam(name = "users") String users,
                                               @RequestParam(name = "slack-invite-link") String link) {
        ProjectProperties current = projectPropertiesService.getOrCreate();
        if (users == null) {
            users = StringUtils.EMPTY;
        }
        current.setSlackDefaultUsers(users);
        if (link == null) {
            link = StringUtils.EMPTY;
        }
        current.setSlackInviteLink(link);
        projectPropertiesService.saveAndFlash(current);
        return ResponseEntity.ok("");
    }

    @PostMapping("/auto-answer")
    public HttpStatus setAutoResponseSettings(@RequestParam(name = "autoAnswerTemplate") Long templateId) {
        ProjectProperties current = projectPropertiesService.getOrCreate();
        if (templateId == null) {
            current.setAutoAnswerTemplate(null);
        } else {
            current.setAutoAnswerTemplate(messageTemplateService.get(templateId));
        }
        projectPropertiesService.update(current);
        return HttpStatus.OK;
    }

    @PostMapping("/birthday")
    public HttpStatus setBirthdayResponseSettings(@RequestParam(name = "birthdayTemplate") Long templateId) {
        ProjectProperties current = projectPropertiesService.getOrCreate();
        if (templateId == null) {
            current.setBirthDayMessageTemplate(null);
        } else {
            current.setBirthDayMessageTemplate(messageTemplateService.get(templateId));
        }
        projectPropertiesService.update(current);
        return HttpStatus.OK;
    }

    @PostMapping("/contractUserSetting")
    public HttpStatus setContractUserSettings(@RequestParam(name = "contractTemplateId") Long templateId,
                                              @RequestParam(name = "contractLastId") Long lastId,
                                              @RequestParam(name = "inn") String inn,
                                              @RequestParam(name = "checkingAccount") String checkingAccount,
                                              @RequestParam(name = "correspondentAccount") String correspondentAccount,
                                              @RequestParam(name = "bankIdentificationCode") String bankIdentificationCode) {
        ProjectProperties current = projectPropertiesService.getOrCreate();
        if (templateId == null) {
            current.setContractTemplate(null);
        } else {
            current.setContractTemplate(messageTemplateService.get(templateId));
        }
        current.setContractLastId(lastId);
        current.setInn(inn);
        current.setCheckingAccount(checkingAccount);
        current.setCorrespondentAccount(correspondentAccount);
        current.setBankIdentificationCode(bankIdentificationCode);
        projectPropertiesService.update(current);
        return HttpStatus.OK;
    }

    @PostMapping("/new-user-status")
    public HttpStatus setNewUserStatus(@RequestParam("statusId") Long statusId) {
        ProjectProperties properties = projectPropertiesService.getOrCreate();
        properties.setNewClientStatus(statusId);
        projectPropertiesService.saveAndFlash(properties);
        return HttpStatus.OK;
    }

    @GetMapping("/repeatedStatus")
    public ResponseEntity<Long> getRepeatedStatus() {
        ProjectProperties projectProperties = projectPropertiesService.getOrCreate();
        Long status = -1L;
        if (projectProperties.getRepeatedDefaultStatusId() != null) {
            status = projectProperties.getRepeatedDefaultStatusId();
        }
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PostMapping("/saveColorByStatus")
    public HttpStatus saveColorByStatus(@RequestParam(name = "colors") String colors) {
        ProjectProperties current = projectPropertiesService.getOrCreate();
        current.setStatusColor(colors);
        projectPropertiesService.update(current);
        return HttpStatus.OK;
    }

    @PostMapping("/new-student-properties")
    public HttpStatus setNewStudentProperties(@RequestParam BigDecimal price, @RequestParam BigDecimal payment, @RequestParam("id") long statusId) {
        ProjectProperties current = projectPropertiesService.getOrCreate();
        current.setDefaultPricePerMonth(price);
        current.setDefaultPayment(payment);
        current.setDefaultStudentStatus(studentStatusService.get(statusId));
        projectPropertiesService.update(current);
        return HttpStatus.OK;
    }

    @PostMapping("/client-default-properties")
    public HttpStatus setClientDefaults(@RequestParam Long repeatedStatus, @RequestParam Long newClientStatus,
                                        @RequestParam Long rejectId, @RequestParam Long firstPayStatus,
                                        @RequestParam Long firstSkypeCallAfterStatus) {
        ProjectProperties current = projectPropertiesService.getOrCreate();
        current.setRepeatedDefaultStatusId(repeatedStatus);
        current.setNewClientStatus(newClientStatus);
        current.setClientRejectStudentStatus(rejectId);
        current.setClientFirstPayStatus(firstPayStatus);
        current.setFirstSkypeCallAfterStatus(firstSkypeCallAfterStatus);
        projectPropertiesService.update(current);
        return HttpStatus.OK;
    }

}