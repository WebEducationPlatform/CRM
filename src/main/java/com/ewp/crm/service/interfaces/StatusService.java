package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Status;
import com.ewp.crm.models.User;
import com.ewp.crm.repository.interfaces.CommonGenericRepository;

import java.util.List;

public interface StatusService {

	List<Status> getAll();

	List<Status> getStatusesWithClientsForUser(User ownerUser);

	Status get(Long id);

	Status get(String name);

	Status getFirstStatusForClient();

	Status getStatusByName(String name);

	void add(Status status);

	void addInit(Status status);

	void update(Status status);

	void delete(Status status);

	void delete(Long id);

	Long findMaxPosition();


}
