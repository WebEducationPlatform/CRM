package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.CallRecord;

public interface CallRecordService extends CommonService<CallRecord> {
	CallRecord addCallRecord(CallRecord callRecord);
	CallRecord getCallRecordWithMaxId();
}
