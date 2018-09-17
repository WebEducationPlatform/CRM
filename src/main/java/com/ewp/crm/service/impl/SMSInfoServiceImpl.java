package com.ewp.crm.service.impl;

import com.ewp.crm.models.SMSInfo;
import com.ewp.crm.repository.interfaces.SMSInfoRepository;
import com.ewp.crm.service.interfaces.SMSInfoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SMSInfoServiceImpl extends CommonServiceImpl<SMSInfo> implements SMSInfoService {
	private final SMSInfoRepository smsInfoRepository;

	public SMSInfoServiceImpl(SMSInfoRepository smsInfoRepository) {
		this.smsInfoRepository = smsInfoRepository;
	}

	@Override
	public SMSInfo addSMSInfo(SMSInfo smsInfo) {
		return smsInfoRepository.saveAndFlush(smsInfo);
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
