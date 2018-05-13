package com.ewp.crm.service.impl;

import com.ewp.crm.models.SMSInfo;
import com.ewp.crm.repository.interfaces.SMSInfoRepository;
import com.ewp.crm.service.interfaces.SMSInfoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SMSInfoServiceImpl implements SMSInfoService {

	private final SMSInfoRepository smsInfoRepository;

	public SMSInfoServiceImpl(SMSInfoRepository smsInfoRepository) {
		this.smsInfoRepository = smsInfoRepository;
	}

	@Override
	public List<SMSInfo> getAllSMS() {
		return smsInfoRepository.findAll();
	}

	@Override
	public List<SMSInfo> getBySMSIsChecked(boolean isChecked) {
		return smsInfoRepository.findByIsChecked(isChecked);
	}

	@Override
	public SMSInfo getById(long id) {
		return smsInfoRepository.findOne(id);
	}

	@Override
	public void updateSMSInfo(SMSInfo smsInfo) {
		smsInfoRepository.save(smsInfo);
	}

	@Override
	public void deleteSMSInfo(long id) {
		smsInfoRepository.delete(id);
	}
}
