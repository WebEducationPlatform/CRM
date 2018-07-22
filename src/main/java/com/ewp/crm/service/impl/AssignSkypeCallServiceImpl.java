package com.ewp.crm.service.impl;

import com.ewp.crm.models.AssignSkypeCall;
import com.ewp.crm.repository.interfaces.AssignSkypeCallRepository;
import com.ewp.crm.service.interfaces.AssignSkypeCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignSkypeCallServiceImpl extends CommonServiceImpl<AssignSkypeCall> implements AssignSkypeCallService {

	private AssignSkypeCallRepository assignSkypeCallRepository;

	@Autowired
	public AssignSkypeCallServiceImpl(AssignSkypeCallRepository assignSkypeCallRepository) {
		this.assignSkypeCallRepository = assignSkypeCallRepository;
	}

	@Override
	public void addSkypeCall(AssignSkypeCall assignSkypeCall) {
		assignSkypeCallRepository.saveAndFlush(assignSkypeCall);
	}

	@Override
	public List<AssignSkypeCall> getSkypeCallDate() {
		return assignSkypeCallRepository.getSkypeCallDate();
	}
}
