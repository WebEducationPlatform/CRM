package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.CallRecord;

public interface CallRecordService {

	CallRecord add (CallRecord callRecord);
	void update(CallRecord callRecord);
	void delete(CallRecord callRecord);
	CallRecord getCallRecord(Long id);
}
