package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Status;

import java.time.ZonedDateTime;
import java.util.Set;

public interface ReportService {
    int countChangedStatusClients(ZonedDateTime firstReportDate, ZonedDateTime lastReportDate, Status from, Status to, Set<Status> exclude);
    int countNewClients(ZonedDateTime firstReportDate, ZonedDateTime lastReportDate);
    long countFirstPaymentClients(Status inProgressStatus, ZonedDateTime firstReportDate, ZonedDateTime lastReportDate);
}
