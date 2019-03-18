package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.ReportsStatus;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.MailSendService;
import com.ewp.crm.service.interfaces.ReportService;
import com.ewp.crm.service.interfaces.ReportsStatusService;
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
    private ReportsStatusService reportsStatusService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private MailSendService mailSendService;

    @PostMapping(value = "/last-days")
    public ResponseEntity clientReportByLastNDays(@RequestBody String date) {
        Optional<String> report = reportService.buildReport(date);
        return report.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
    }

    @PostMapping(value = "/getReportsStatus")
    public ResponseEntity<ReportsStatus> getReportsStatus(){
        List<ReportsStatus> listReportStatus = reportsStatusService.getAll();
        return ResponseEntity.ok(listReportStatus.size() == 0 ? null : listReportStatus.get(0));
    }

    @PostMapping(value = "/setReportsStatus")
    public ResponseEntity updateReportsStatus(@Valid @RequestBody ReportsStatus reportsStatus){
        List<ReportsStatus> listReportStatus = reportsStatusService.getAll();
        ReportsStatus currentReportsStatus = listReportStatus.size() == 0 ? new ReportsStatus() : listReportStatus.get(0);
        currentReportsStatus.setDropOutStatus(reportsStatus.getDropOutStatus());
        currentReportsStatus.setEndLearningStatus(reportsStatus.getEndLearningStatus());
        currentReportsStatus.setInLearningStatus(reportsStatus.getInLearningStatus());
        currentReportsStatus.setPauseLearnStatus(reportsStatus.getPauseLearnStatus());
        currentReportsStatus.setTrialLearnStatus(reportsStatus.getTrialLearnStatus());
        reportsStatusService.update(currentReportsStatus);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping(value = "/sendReportToEmail")
    public ResponseEntity sendReportToEmail(@Valid @RequestParam String report,
                                            @Valid @RequestParam String email){
        User user = new User();
        user.setEmail(email);
        mailSendService.sendNotificationMessage(user, report);
        return  ResponseEntity.ok(HttpStatus.OK);
    }
}
