package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.AutoAnswer;
import com.ewp.crm.models.MessageTemplate;
import com.ewp.crm.models.Status;
import com.ewp.crm.models.User;
import com.ewp.crm.models.dto.AutoAnswerDto;
import com.ewp.crm.service.interfaces.AutoAnswersService;
import com.ewp.crm.service.interfaces.MessageTemplateService;
import com.ewp.crm.service.interfaces.StatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/rest/autoanswers")
@PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER')")
public class AutoAnswersRestController {

    private static Logger logger = LoggerFactory.getLogger(AutoAnswersRestController.class);
    private final AutoAnswersService autoAnswersService;
    private final MessageTemplateService messageTemplateService;
    private final StatusService statusService;

    @Autowired
    public AutoAnswersRestController(AutoAnswersService autoAnswersService, MessageTemplateService messageTemplateService, StatusService statusService) {
        this.autoAnswersService = autoAnswersService;
        this.messageTemplateService = messageTemplateService;
        this.statusService = statusService;
    }

    @GetMapping(value = "/get/{id}")
    public ResponseEntity getAutoAnswerById(@PathVariable("id") Long id){

        AutoAnswer autoAnswer = autoAnswersService.get(id);
        return  ResponseEntity.ok(new AutoAnswerDto(autoAnswer));
    }

    @PostMapping(value = "/add")
    public HttpStatus addNewAutoAnswer(
            @RequestParam(name = "id") Long autoanswer_id,
            @RequestParam(name = "subject") String subject,
            @RequestParam(name = "messageTemplate") Long messageTemplate_id,
            @RequestParam(name = "status") Long status_id,
            @AuthenticationPrincipal User currentAdmin) {

        HttpStatus status = HttpStatus.OK;
        AutoAnswer autoAnswer = autoAnswersService.add(subject, messageTemplate_id, status_id);
        logger.info("{} has added autoanswer theme: {}", currentAdmin.getFullName(), autoAnswer.getSubject());
        return status;
    }

    @PostMapping(value = "/delete")
    public HttpStatus addNewAutoAnswer(
            @RequestParam(name = "autoanswer_id") Long autoanswer_id,
            @AuthenticationPrincipal User currentAdmin) {

        HttpStatus status = HttpStatus.OK;
        autoAnswersService.delete(autoanswer_id);
        logger.info("{} has deleted autoanswer theme: {}", currentAdmin.getFullName(), autoanswer_id);
        return status;
    }

    @PostMapping(value = "/update")
    public HttpStatus updateNewAutoAnswer(
            @RequestParam(name = "id") Long autoanswer_id,
            @RequestParam(name = "subject") String subject,
            @RequestParam(name = "messageTemplate") Long messageTemplate_id,
            @RequestParam(name = "status") Long status_id,
            @AuthenticationPrincipal User currentAdmin) {

        HttpStatus httpStatus = HttpStatus.OK;
        AutoAnswer autoAnswerFromDB = autoAnswersService.get(autoanswer_id);

        MessageTemplate messageTemplate = messageTemplateService.get(messageTemplate_id);
        Optional<Status> status = statusService.get(status_id);
        if (autoAnswerFromDB!= null && messageTemplate != null && status.isPresent()){
            autoAnswerFromDB.setSubject(subject);
            autoAnswerFromDB.setMessageTemplate(messageTemplate);
            autoAnswerFromDB.setStatus(status.get());

            autoAnswersService.update(autoAnswerFromDB);
            logger.info("{} has updated autoanswer theme: {}", currentAdmin.getFullName(), autoAnswerFromDB.getSubject());
        }else {
            httpStatus = HttpStatus.NOT_FOUND;
        }

        return httpStatus;
    }

}
