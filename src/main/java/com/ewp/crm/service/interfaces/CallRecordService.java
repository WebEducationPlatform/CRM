package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.CallRecord;

import java.util.Optional;

public interface CallRecordService extends CommonService<CallRecord> {
	Optional<CallRecord> addCallRecord(CallRecord callRecord);
}
