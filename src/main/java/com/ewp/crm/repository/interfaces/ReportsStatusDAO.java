package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.ReportsStatus;

import java.util.List;

public interface ReportsStatusDAO extends CommonGenericRepository<ReportsStatus> {

    List<ReportsStatus> findAll();

}
