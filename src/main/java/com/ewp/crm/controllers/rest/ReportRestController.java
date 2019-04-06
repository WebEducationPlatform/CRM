package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Status;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.MailSendService;
import com.ewp.crm.service.interfaces.ReportService;
import com.ewp.crm.service.interfaces.StatusService;
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
import java.util.Optional;


@RestController
@RequestMapping(value = "/rest/report")
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
public class ReportRestController {

    private final ReportService reportService;
    private final StatusService statusService;
    private final MailSendService mailSendService;

    @Autowired
    public ReportRestController(ReportService reportService, MailSendService mailSendService, StatusService statusService) {
        this.reportService = reportService;
        this.mailSendService = mailSendService;
        this.statusService = statusService;
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
        if (result >= 0) {
            return ResponseEntity.ok(result);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/countNew")
    public ResponseEntity countNew(@RequestParam String firstReportDate,
                                   @RequestParam String lastReportDate,
                                   @RequestParam(required = false) List<Long> excludeIds) {
        return ResponseEntity.ok(reportService.countNewClients(
                getZonedDateTimeFromString(firstReportDate),
                getZonedDateTimeFromString(lastReportDate), excludeIds)
        );
    }

    @GetMapping(value = "/countFirstPayments")
    public ResponseEntity countFirstPayments(@RequestParam String firstReportDate,
                                             @RequestParam String lastReportDate,
                                             @RequestParam(required = false) List<Long> excludeIds) {
        Optional<Status> status = statusService.get(4L);
        if (status.isPresent()) {
            return ResponseEntity.ok(reportService.countFirstPaymentClients(
                    status.get(),
                    getZonedDateTimeFromString(firstReportDate),
                    getZonedDateTimeFromString(lastReportDate)));
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
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
