package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.CallRecord;
import com.ewp.crm.models.User;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface CallRecordService extends CommonService<CallRecord> {

	List<CallRecord> getAllCommonRecords(Pageable pageable);

	Optional<CallRecord> addCallRecord(CallRecord callRecord);

	Optional<CallRecord> addCallRecordTo(CallRecord callRecord, User user, String to);

	public CallRecord updateCallRecord(CallRecord callRecord);

	List<CallRecord> findAllByCallingUserAndDateBetween(User user, ZonedDateTime from, ZonedDateTime to, Pageable pageable);

    List<CallRecord> findAllByDateBetween(ZonedDateTime from, ZonedDateTime to, Pageable pageable);

	Optional<CallRecord> getByClientHistory_Id(Long id);
}
