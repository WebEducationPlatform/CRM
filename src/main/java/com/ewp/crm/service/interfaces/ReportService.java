package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Status;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

public interface ReportService {
    int countChangedStatusClients(ZonedDateTime firstReportDate, ZonedDateTime lastReportDate, Long from, Long to, List<Long> excludeStatusesIds);
    long countNewClients(ZonedDateTime firstReportDate, ZonedDateTime lastReportDate, List<Long> excludeStatusesIds);
    long countFirstPaymentClients(Status inProgressStatus, ZonedDateTime firstReportDate, ZonedDateTime lastReportDate);
}
