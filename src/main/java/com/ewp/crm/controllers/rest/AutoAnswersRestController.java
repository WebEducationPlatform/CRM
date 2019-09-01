package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.AutoAnswer;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.AutoAnswersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/autoanswers")
@PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER')")
public class AutoAnswersRestController {

    private static Logger logger = LoggerFactory.getLogger(AutoAnswersRestController.class);
    private final AutoAnswersService autoAnswersService;

    @Autowired
    public AutoAnswersRestController(AutoAnswersService autoAnswersService) {
        this.autoAnswersService = autoAnswersService;
    }

    @PostMapping(value = "/add")
    public HttpStatus addNewAutoAnswer(
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


}
