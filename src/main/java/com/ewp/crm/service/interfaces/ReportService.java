package com.ewp.crm.service.interfaces;

import java.util.Optional;

public interface ReportService {

    Optional<String> buildReport(String date);

    Optional<String> buildReportOfLastMonth();
}
