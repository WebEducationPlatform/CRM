package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.SMSInfo;

import java.util.List;

public interface SMSInfoRepository extends CommonGenericRepository<SMSInfo> {

	List<SMSInfo> getByIsChecked(Boolean isDelivered);

	List<SMSInfo> findAll();
}
