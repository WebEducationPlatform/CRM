package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Status;

public interface StatusService {
	Status getStatusByName(String name);

	void addStatus(Status status);
}
