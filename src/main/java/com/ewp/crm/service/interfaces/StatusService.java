package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Status;

import java.util.List;

public interface StatusService {

	List<Status> getAll();

	Status get(Long id);

	Status get(String name);

	void add(Status status);

	void update(Status status);

	void delete(Status status);

	void delete(Long id);

	void changeClientStatus(Long clientId, Long statusId);
}
