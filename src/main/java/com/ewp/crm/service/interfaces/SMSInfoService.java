package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.SMSInfo;

import java.util.List;

public interface SMSInfoService {

	List<SMSInfo> getAllSMS();

	List<SMSInfo> getBySMSIsChecked(boolean isChecked);

	SMSInfo getById(long id);

	void updateSMSInfo(SMSInfo smsInfo);

	void deleteSMSInfo(long id);
}
