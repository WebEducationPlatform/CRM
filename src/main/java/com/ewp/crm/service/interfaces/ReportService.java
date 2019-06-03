package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface ReportService {
    Report getAllChangedStatusClientsByDate(ZonedDateTime reportStartDate, ZonedDateTime reportEndDate, long fromStatusId, long toStatusId, List<Long> excludeStatusesIds);

    Report getAllNewClientsByDate(ZonedDateTime firstReportDate, ZonedDateTime lastReportDate, List<Long> excludeStatusesIds);

    Report getAllFirstPaymentClientsByDate(ZonedDateTime firstReportDate, ZonedDateTime lastReportDate, List<Long> excludeStatusesIds);

    Optional<String> getFileName(List<String> selectedCheckboxes, String delimeter, Status status);

    void writeToFileWithConditionToDonwload(ConditionToDownload conditionToDowbload, String fileName);

    void writeToFileWithFilteringConditions(FilteringCondition filteringCondition, String fileName);
}
