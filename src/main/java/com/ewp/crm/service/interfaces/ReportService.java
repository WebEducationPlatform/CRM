package com.ewp.crm.service.interfaces;

import java.time.ZonedDateTime;
import java.util.List;

public interface ReportService {
    int countChangedStatusClients(ZonedDateTime reportStartDate, ZonedDateTime reportEndDate, long fromStatusId, long toStatusId, List<Long> excludeStatusesIds);
    int countNewClients(ZonedDateTime firstReportDate, ZonedDateTime lastReportDate, List<Long> excludeStatusesIds);
    long countFirstPaymentClients(ZonedDateTime firstReportDate, ZonedDateTime lastReportDate, List<Long> excludeStatusesIds);
}
