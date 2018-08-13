package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.ReportsStatus;
import com.ewp.crm.models.User;
import com.ewp.crm.report.ReportService;
import com.ewp.crm.repository.interfaces.ClientRepository;
import com.ewp.crm.service.email.MailSendService;
import com.ewp.crm.service.interfaces.ReportsStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping(value = "/rest/report")
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
public class ReportRestController {

    @Autowired
    private ReportsStatusService reportsStatusService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private MailSendService mailSendService;

    @RequestMapping(value = "/last-days", method = RequestMethod.POST)
    public ResponseEntity clientReportByLastNDays(@RequestBody String date) {
        return ResponseEntity.ok(reportService.buildReport(date));
    }

    @RequestMapping(value = "/getReportsStatus", method = RequestMethod.POST)
    public ResponseEntity<ReportsStatus> getReportsStatus(){
        return ResponseEntity.ok(reportsStatusService.getAll().get(0));
    }

    @RequestMapping(value = "/setReportsStatus", method = RequestMethod.POST)
    public ResponseEntity updateReportsStatus(@Valid @RequestBody ReportsStatus reportsStatus){
        ReportsStatus currentReportsStatus = reportsStatusService.getAll().get(0);
        currentReportsStatus.setDropOutStatus(reportsStatus.getDropOutStatus());
        currentReportsStatus.setEndLearningStatus(reportsStatus.getEndLearningStatus());
        currentReportsStatus.setInLearningStatus(reportsStatus.getInLearningStatus());
        reportsStatusService.update(currentReportsStatus);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @RequestMapping(value = "/sendReportToEmail", method = RequestMethod.POST)
    public ResponseEntity sendReportToEmail(@RequestParam String report,
                                            @RequestParam String email){
        User user = new User();
        user.setEmail(email);
        mailSendService.sendNotificationMessage(user, report);
        return  ResponseEntity.ok(HttpStatus.OK);
    }
}
