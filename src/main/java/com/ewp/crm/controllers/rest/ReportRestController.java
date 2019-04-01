package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.ReportsStatus;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.MailSendService;
import com.ewp.crm.service.interfaces.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping(value = "/rest/report")
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
public class ReportRestController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private MailSendService mailSendService;

    @PostMapping(value = "/sendReportToEmail")
    public ResponseEntity sendReportToEmail(@Valid @RequestParam String report,
                                            @Valid @RequestParam String email){
        User user = new User();
        user.setEmail(email);
        mailSendService.sendNotificationMessage(user, report);
        return  ResponseEntity.ok(HttpStatus.OK);
    }
}
