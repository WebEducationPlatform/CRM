package com.ewp.crm.service.impl;

import com.ewp.crm.models.CallRecord;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.User;
import com.ewp.crm.repository.interfaces.CallRecordRepository;
import com.ewp.crm.service.interfaces.CallRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class CallRecordServiceImpl extends CommonServiceImpl<CallRecord> implements CallRecordService {

    private final CallRecordRepository callRecordRepository;
    private static final String CALLED_TO = "позвонил по номеру";
    private static final String UNKNOWN_USER = "Звонок не из CRM";

    @Autowired
    public CallRecordServiceImpl(CallRecordRepository callRecordRepository) {
        this.callRecordRepository = callRecordRepository;
    }

    @Override
    public List<CallRecord> getAllCommonRecords(Pageable pageable) {
        return callRecordRepository.getAllCommonRecords(pageable);
    }

    @Override
    public Optional<CallRecord> addCallRecord(CallRecord callRecord) {
        Optional<ClientHistory> clientHistory = Optional.ofNullable(callRecord.getClientHistory());
        if (clientHistory.isPresent()) {
            return Optional.of(callRecordRepository.saveAndFlush(callRecord));
        }
        callRecord.setDate(ZonedDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        callRecord.setComment(UNKNOWN_USER);
        return Optional.of(callRecordRepository.saveAndFlush(callRecord));
    }

    @Override
    public Optional<CallRecord> addCallRecord(CallRecord callRecord, User user, String to) {
        callRecord.setDate(ZonedDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        callRecord.setComment(user.getFullName() + " " + CALLED_TO + " " + to);
        return Optional.of(callRecordRepository.saveAndFlush(callRecord));
    }
}
