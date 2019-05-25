package com.ewp.crm.service.impl;

import com.ewp.crm.models.SMSInfo;
import com.ewp.crm.repository.interfaces.SMSInfoRepository;
import com.ewp.crm.service.interfaces.SMSInfoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SMSInfoServiceImpl extends CommonServiceImpl<SMSInfo> implements SMSInfoService {
	private final SMSInfoRepository smsInfoRepository;

	public SMSInfoServiceImpl(SMSInfoRepository smsInfoRepository) {
		this.smsInfoRepository = smsInfoRepository;
	}

	@Override
	public Optional<SMSInfo> addSMSInfo(SMSInfo smsInfo) {
		return Optional.of(smsInfoRepository.saveAndFlush(smsInfo));
	}

	@Override
	public void deleteAllSMSByUserId(Long id) {
		smsInfoRepository.deleteAllByUserId(id);
	}

	@Override
	public List<SMSInfo> getAllSMS() {
		return smsInfoRepository.findAll();
	}

	@Override
	public List<SMSInfo> getSMSByIsChecked(boolean isChecked) {
		return smsInfoRepository.getByIsChecked(isChecked);
	}
}
