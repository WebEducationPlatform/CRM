package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.ReportsStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportsStatusDAO extends CommonGenericRepository<ReportsStatus> {

    List<ReportsStatus> findAll();

}
