package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.CallRecord;
import com.ewp.crm.models.User;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface CallRecordService extends CommonService<CallRecord> {

	List<CallRecord> getAllCommonRecords(Pageable pageable);

	Optional<CallRecord> addCallRecord(CallRecord callRecord);

	Optional<CallRecord> addCallRecord(CallRecord callRecord, User user, String to);

}
