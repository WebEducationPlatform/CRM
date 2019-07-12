package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.ClientStatusChangingHistoryService;
import com.ewp.crm.service.interfaces.MailSendService;
import com.ewp.crm.service.interfaces.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'HR')")
public class ReportRestController {

    private final ReportService reportService;
    private final MailSendService mailSendService;
    private final ClientStatusChangingHistoryService clientStatusChangingHistoryService;

    @Autowired
    public ReportRestController(ReportService reportService, MailSendService mailSendService,
                                ClientStatusChangingHistoryService clientStatusChangingHistoryService) {
        this.reportService = reportService;
        this.mailSendService = mailSendService;
        this.clientStatusChangingHistoryService = clientStatusChangingHistoryService;
    }

    private ZonedDateTime getZonedDateTimeFromString(String date) {
        return ZonedDateTime.of(LocalDate.parse(date, DateTimeFormatter.ISO_DATE).atStartOfDay(), ZoneId.systemDefault());
    }

//    // Temporary method to fill new entity's table
//    @GetMapping(value = "/init")
//    public ResponseEntity init() {
//        reportService.fillClientStatusChangingHistoryFromClientHistory();
//        return new ResponseEntity(HttpStatus.OK);
//    }

//    // Temporary method to fill new entity's table
//    @GetMapping(value = "/links")
//    public ResponseEntity links() {
//        reportService.processLinksInStatusChangingHistory();
//        return new ResponseEntity(HttpStatus.OK);
//    }

//    // Temporary method to fill new entity's table
//    @GetMapping(value = "/set-creations")
//    public ResponseEntity setCreations() {
//        reportService.setCreationsInStatusChangingHistory();
//        return new ResponseEntity(HttpStatus.OK);
//    }

    @GetMapping(value = "/mark-fakes")
    public ResponseEntity markFakes() {
        clientStatusChangingHistoryService.markAllFakeStatusesByChangingInIntervalRule(3);
        clientStatusChangingHistoryService.markAllFakeStatusesByReturningInIntervalRule(24);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity count(@RequestParam String firstReportDate,
                                @RequestParam String lastReportDate,
                                @RequestParam Long fromId,
                                @RequestParam Long toId,
                                @RequestParam(required = false) List<Long> excludeIds) {
        return ResponseEntity.ok(reportService.getAllChangedStatusClientsByDate(
                getZonedDateTimeFromString(firstReportDate),
                getZonedDateTimeFromString(lastReportDate),
                fromId,
                toId,
                excludeIds));
    }

    @GetMapping(value = "/countFromAny", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity countFromAny(@RequestParam String firstReportDate,
                                @RequestParam String lastReportDate,
                                @RequestParam Long toId,
                                @RequestParam(required = false) List<Long> excludeIds) {
        return ResponseEntity.ok(reportService.getAllChangedStatusClientsByDate(
                getZonedDateTimeFromString(firstReportDate),
                getZonedDateTimeFromString(lastReportDate),
                toId,
                excludeIds));
    }

    @GetMapping(value = "/countNew", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity countNew(@RequestParam String firstReportDate,
                                   @RequestParam String lastReportDate,
                                   @RequestParam(required = false) List<Long> excludeIds,
                                   @RequestParam(required = false) Long newStatusId) {
        if (newStatusId == null) {
            return ResponseEntity.ok(reportService.getAllNewClientsByDate(
                    getZonedDateTimeFromString(firstReportDate),
                    getZonedDateTimeFromString(lastReportDate),
                    excludeIds));
        }
        return ResponseEntity.ok(reportService.getAllNewClientsByDateAndFirstStatus(
                getZonedDateTimeFromString(firstReportDate),
                getZonedDateTimeFromString(lastReportDate),
                excludeIds,
                newStatusId));
    }

    @GetMapping(value = "/countFirstPayments", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity countFirstPayments(@RequestParam String firstReportDate,
                                             @RequestParam String lastReportDate,
                                             @RequestParam(required = false) List<Long> excludeIds) {
        return ResponseEntity.ok(reportService.getAllFirstPaymentClientsByDate(
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
