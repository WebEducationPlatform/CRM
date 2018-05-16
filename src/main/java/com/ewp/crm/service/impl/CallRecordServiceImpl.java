package com.ewp.crm.service.impl;

import com.ewp.crm.models.CallRecord;
import com.ewp.crm.repository.interfaces.CallRecordRepository;
import com.ewp.crm.service.interfaces.CallRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CallRecordServiceImpl implements CallRecordService {

	private final CallRecordRepository callRecordRepository;

	@Autowired
	public CallRecordServiceImpl(CallRecordRepository callRecordRepository) {
		this.callRecordRepository = callRecordRepository;
	}

	@Override
	public CallRecord add(CallRecord callRecord) {
		return callRecordRepository.saveAndFlush(callRecord);
	}

	@Override
	public void update(CallRecord callRecord) {
		callRecordRepository.saveAndFlush(callRecord);
	}

	@Override
	public void delete(CallRecord callRecord) {
		callRecordRepository.delete(callRecord);
	}

	@Override
	public CallRecord getCallRecord(Long id) {
		return callRecordRepository.getCallRecordById(id);
	}
}
