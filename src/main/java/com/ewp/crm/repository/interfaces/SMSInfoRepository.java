package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.SMSInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SMSInfoRepository extends JpaRepository<SMSInfo, Long> {
	List<SMSInfo> findByIsDelivered(Boolean isDelivered);
	List<SMSInfo> findAll();
}
