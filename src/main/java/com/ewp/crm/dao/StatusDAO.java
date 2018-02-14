package com.ewp.crm.dao;

import com.ewp.crm.models.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusDAO extends JpaRepository<Status,Long>{
    Status findStatusByName(String name);
}
