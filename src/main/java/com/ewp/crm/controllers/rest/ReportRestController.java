package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.MailSendService;
import com.ewp.crm.service.interfaces.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@RestController
@RequestMapping(value = "/rest/report")
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
public class ReportRestController {

    private final ReportService reportService;
    private final MailSendService mailSendService;

    @Autowired
    public ReportRestController(ReportService reportService, MailSendService mailSendService) {
        this.reportService = reportService;
        this.mailSendService = mailSendService;
    }

    private ZonedDateTime getZonedDateTimeFromString(String date) {
        return ZonedDateTime.of(LocalDate.parse(date, DateTimeFormatter.ISO_DATE).atStartOfDay(), ZoneId.systemDefault());
    }

    @GetMapping(value = "/count")
    public ResponseEntity count(@RequestParam String firstReportDate,
                                @RequestParam String lastReportDate,
                                @RequestParam Long fromId,
                                @RequestParam Long toId,
                                @RequestParam(required = false) List<Long> excludeIds) {
        int result = reportService.countChangedStatusClients(
                getZonedDateTimeFromString(firstReportDate),
                getZonedDateTimeFromString(lastReportDate),
                fromId,
                toId,
                excludeIds);
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/countNew")
    public ResponseEntity countNew(@RequestParam String firstReportDate,
                                   @RequestParam String lastReportDate,
                                   @RequestParam(required = false) List<Long> excludeIds) {
        return ResponseEntity.ok(reportService.countNewClients(
                getZonedDateTimeFromString(firstReportDate),
                getZonedDateTimeFromString(lastReportDate),
                excludeIds));
    }

    @GetMapping(value = "/countFirstPayments")
    public ResponseEntity countFirstPayments(@RequestParam String firstReportDate,
                                             @RequestParam String lastReportDate,
                                             @RequestParam(required = false) List<Long> excludeIds) {
        return ResponseEntity.ok(reportService.countFirstPaymentClients(
                getZonedDateTimeFromString(firstReportDate),
                getZonedDateTimeFromString(lastReportDate),
                excludeIds));
    }

    @PostMapping(value = "/sendReportToEmail")
    public ResponseEntity sendReportToEmail(@Valid @RequestParam String report,
                                            @Valid @RequestParam String email) {
        User user = new User();
        user.setEmail(email);
        mailSendService.sendNotificationMessage(user, report);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
