package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.*;
import com.ewp.crm.models.dto.ReportDto;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface ReportService {
    ReportDto getAllChangedStatusClientsByDate(ZonedDateTime reportStartDate, ZonedDateTime reportEndDate, long fromStatusId, long toStatusId, List<Long> excludeStatusesIds);

    ReportDto getAllChangedStatusClientsByDate(ZonedDateTime reportStartDate, ZonedDateTime reportEndDate, long toStatusId, List<Long> excludeStatusesIds);

    ReportDto getAllNewClientsByDate(ZonedDateTime firstReportDate, ZonedDateTime lastReportDate, List<Long> excludeStatusesIds);

    ReportDto getAllNewClientsByDateAndFirstStatus(ZonedDateTime reportStartDate, ZonedDateTime reportEndDate, List<Long> excludeStatusesIds, Long firstStatusId);

    ReportDto getAllFirstPaymentClientsByDate(ZonedDateTime firstReportDate, ZonedDateTime lastReportDate, List<Long> excludeStatusesIds);

    Optional<String> getFileName(List<String> selectedCheckboxes, String delimeter, String filetype, Status status);

    void writeToFileWithFilteringConditions(FilteringCondition filteringCondition, String fileName);

    void writeToFileWithConditionToDownload(ConditionToDownload conditionToDownload, String fileName);

    void writeToExcelFileWithFilteringConditions(FilteringCondition filteringCondition, String fileName);

    void writeToExcelFileWithConditionToDownload(ConditionToDownload conditionToDowbload, String fileName);

    void writeToCSVFileWithFilteringConditions(FilteringCondition filteringCondition, String fileName);

    void writeToCSVFileWithConditionToDownload(ConditionToDownload conditionToDownload, String fileName);

    void fillClientStatusChangingHistoryFromClientHistory();

    void processLinksInStatusChangingHistory();

    void setCreationsInStatusChangingHistory();

    String fillExcelOrCsvFileForBitrix24(String formatFile, List<Long> statusIds);
}
