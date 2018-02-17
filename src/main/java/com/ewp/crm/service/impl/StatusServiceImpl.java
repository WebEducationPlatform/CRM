package com.ewp.crm.service.impl;

import com.ewp.crm.models.Status;
import com.ewp.crm.repository.interfaces.StatusDAO;
import com.ewp.crm.service.interfaces.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatusServiceImpl implements StatusService {

	@Autowired
	private StatusDAO statusDAO;

	@Override
	public Status getStatusByName(String name) {
		return statusDAO.findStatusByName(name);
	}
}
