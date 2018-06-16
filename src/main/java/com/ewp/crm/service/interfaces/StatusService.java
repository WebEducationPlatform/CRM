package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Status;
import com.ewp.crm.models.User;

import java.util.List;

public interface StatusService {

	List<Status> getAll();

	List<Status> getStatusesWithClientsForUser(User ownerUser);

	Status get(Long id);

	Status get(String name);

	Status getFirstStatusForClient();

	void add(Status status);

	void update(Status status);

	void delete(Status status);

	void delete(Long id);
}
