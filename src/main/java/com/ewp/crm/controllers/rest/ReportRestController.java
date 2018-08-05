package com.ewp.crm.controllers.rest;

import com.ewp.crm.report.Report;
import com.ewp.crm.models.Client;
import com.ewp.crm.report.ReportData;
import com.ewp.crm.repository.interfaces.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReportRestController {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private Report report;

    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
    @RequestMapping(value = "/last-days", method = RequestMethod.POST)
    public ResponseEntity clientReportByLastNDays(@RequestBody ReportData reportData) {
        //List<Client> clients = clientRepository.getClientByTimeInterval(reportData.getDays());
        report.countByLastDays(reportData.getDays());
        return ResponseEntity.ok("");
    }
}
