package com.ewp.crm.service.impl;

import com.ewp.crm.models.CallRecord;
import com.ewp.crm.repository.interfaces.CallRecordRepository;
import com.ewp.crm.service.interfaces.CallRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CallRecordServiceImpl extends CommonServiceImpl<CallRecord> implements CallRecordService {
    private final CallRecordRepository callRecordRepository;

    @Autowired
    public CallRecordServiceImpl(CallRecordRepository callRecordRepository) {
        this.callRecordRepository = callRecordRepository;
    }

    @Override
    public CallRecord addCallRecord(CallRecord callRecord) {
        return callRecordRepository.saveAndFlush(callRecord);
    }

}
