package com.ewp.crm.service.impl;

import com.ewp.crm.exceptions.status.StatusExistsException;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.Status;
import com.ewp.crm.models.User;
import com.ewp.crm.repository.interfaces.ClientRepository;
import com.ewp.crm.repository.interfaces.StatusDAO;
import com.ewp.crm.service.interfaces.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class StatusServiceImpl implements StatusService {

	private final StatusDAO statusDAO;

	@Autowired
	public StatusServiceImpl(StatusDAO statusDAO) {
		this.statusDAO = statusDAO;
	}

	@Override
	public List<Status> getAll() {
		return statusDAO.findAllByOrderByIdAsc();
	}

	@Override
	public List<Status> getStatusesWithClientsForUser(User ownerUser) {
		List<Status> statuses = getAll();
		for (Status status : statuses) {
			List<Client> filteredClients = new ArrayList<>();
			for (Client client : status.getClients()) {
				if (client.getOwnerUser() == null || ownerUser.equals(client.getOwnerUser())) {
					filteredClients.add(client);
				}
			}
			status.setClients(filteredClients);
		}
		return statuses;
	}

	@Override
	public Status get(Long id) {
		return statusDAO.findOne(id);
	}

	@Override
	public Status get(String name) {
		return statusDAO.findStatusByName(name);
	}

	@Override
	public Status getFirstStatusForClient() {
		Status check = statusDAO.findOne(2L);
		if (check == null) {
			return statusDAO.findOne(1L);
		}
		return check;
	}

	@Override
	public void add(Status status) {
		checkStatusUnique(status);
		statusDAO.saveAndFlush(status);
	}

	@Override
	public void update(Status status) {
		checkStatusUnique(status);
		statusDAO.saveAndFlush(status);
	}

	private void checkStatusUnique(Status status) {
		Status statusFromDB = statusDAO.findStatusByName(status.getName());
		if (statusFromDB != null && !statusFromDB.equals(status)) {
			throw new StatusExistsException("Статус с таким названием уже существует");
		}
	}

	private void checkStatusId(Long id) {
		if (id == 1L) {
			throw new StatusExistsException("Статус с индексом \"1\" нельзя удалить");
		}
		if (statusDAO.findOne(id).getName().equals("default")) {
			throw new StatusExistsException("Статус default нельзя удалить");
		}
	}

	private void transferStatusClientsBeforeDelete (Status status) {
		if (status.getClients() != null) {
			Status defaultStatus = statusDAO.findStatusByName("default");
			defaultStatus.getClients().addAll(status.getClients());
			status.getClients().clear();
			statusDAO.saveAndFlush(status);
		}
	}

	@Override
	public void delete(Status status) {
		delete(status.getId());
	}

	@Override
	public void delete(Long id) {
		checkStatusId(id);
		Status statusFromDB = statusDAO.findOne(id);
		transferStatusClientsBeforeDelete(statusFromDB);
		statusDAO.delete(statusFromDB);
	}

	@Override
	public void changeClientStatus(Client client, Long statusId) {
		//TODO решить что делать
	}

}
