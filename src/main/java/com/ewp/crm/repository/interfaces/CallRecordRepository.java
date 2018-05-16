package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.CallRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CallRecordRepository extends JpaRepository<CallRecord, Long> {

	CallRecord getCallRecordById(Long id);
}
