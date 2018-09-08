package com.ewp.crm.service.interfaces;

public interface ReportService {

    String buildReport(String date);

    String buildReportOfLastMonth();
}
