package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.ConditionToDownload;
import com.ewp.crm.models.FilteringCondition;
import com.ewp.crm.models.Status;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface ReportService {
    int countChangedStatusClients(ZonedDateTime reportStartDate, ZonedDateTime reportEndDate, long fromStatusId, long toStatusId, List<Long> excludeStatusesIds);

    int countNewClients(ZonedDateTime firstReportDate, ZonedDateTime lastReportDate, List<Long> excludeStatusesIds);

    long countFirstPaymentClients(ZonedDateTime firstReportDate, ZonedDateTime lastReportDate, List<Long> excludeStatusesIds);

    Optional<String> getFileName(List<String> selectedCheckboxes, String delimeter, Status status);

    void writeToFileWithConditionToDonwload(ConditionToDownload conditionToDowbload, String fileName);

    void writeToFileWithFilteringConditions(FilteringCondition filteringCondition, String fileName);
}
