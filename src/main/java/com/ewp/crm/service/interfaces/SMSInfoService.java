package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.SMSInfo;

import java.util.List;
import java.util.Optional;

public interface SMSInfoService extends CommonService<SMSInfo> {

	List<SMSInfo> getAllSMS();

	List<SMSInfo> getSMSByIsChecked(boolean isChecked);

	Optional<SMSInfo> addSMSInfo(SMSInfo smsInfo);

	void deleteAllSMSByUserId(Long id);
}
