package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface ReportService {
    Report getAllChangedStatusClientsByDate(ZonedDateTime reportStartDate, ZonedDateTime reportEndDate, long fromStatusId, long toStatusId, List<Long> excludeStatusesIds);

    Report getAllNewClientsByDate(ZonedDateTime firstReportDate, ZonedDateTime lastReportDate, List<Long> excludeStatusesIds);

    Report getAllFirstPaymentClientsByDate(ZonedDateTime firstReportDate, ZonedDateTime lastReportDate, List<Long> excludeStatusesIds);

    Optional<String> getFileName(List<String> selectedCheckboxes, String delimeter, String filetype, Status status);

    void writeToFileWithFilteringConditions(FilteringCondition filteringCondition, String fileName);

    void writeToFileWithConditionToDownload(ConditionToDownload conditionToDownload, String fileName);

    void writeToExcelFileWithFilteringConditions(FilteringCondition filteringCondition, String fileName);

    void writeToExcelFileWithConditionToDownload(ConditionToDownload conditionToDowbload, String fileName);

    void writeToCSVFileWithFilteringConditions(FilteringCondition filteringCondition, String fileName);

    void writeToCSVFileWithConditionToDownload(ConditionToDownload conditionToDownload, String fileName);
}
