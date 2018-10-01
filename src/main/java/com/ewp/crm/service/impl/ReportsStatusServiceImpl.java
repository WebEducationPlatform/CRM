package com.ewp.crm.service.impl;

import com.ewp.crm.models.ReportsStatus;
import com.ewp.crm.repository.interfaces.ReportsStatusDAO;
import com.ewp.crm.service.interfaces.ReportsStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportsStatusServiceImpl extends CommonServiceImpl<ReportsStatus> implements ReportsStatusService{

    private ReportsStatusDAO reportsStatusDAO;

    @Autowired
    public ReportsStatusServiceImpl(ReportsStatusDAO reportsStatusDAO) {
        this.reportsStatusDAO = reportsStatusDAO;
    }
}
