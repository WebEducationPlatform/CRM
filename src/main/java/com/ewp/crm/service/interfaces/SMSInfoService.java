package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.SMSInfo;

import java.util.List;

public interface SMSInfoService {
	List<SMSInfo> getAllSMS();
	List<SMSInfo> getSMSbyDelivery(Boolean isDelivered);
	void updateSMSInfo(SMSInfo smsInfo);
	void deleteSMSInfo(long id);
}
