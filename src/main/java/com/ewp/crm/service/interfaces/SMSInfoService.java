package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.SMSInfo;

import java.util.List;

public interface SMSInfoService extends CommonService<SMSInfo> {

	List<SMSInfo> getAllSMS();

	List<SMSInfo> getSMSByIsChecked(boolean isChecked);

	SMSInfo addSMSInfo(SMSInfo smsInfo);
}
