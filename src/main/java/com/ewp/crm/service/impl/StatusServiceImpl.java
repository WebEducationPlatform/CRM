package com.ewp.crm.service.impl;

import com.ewp.crm.exceptions.status.StatusDataException;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.Status;
import com.ewp.crm.repository.interfaces.ClientDAO;
import com.ewp.crm.repository.interfaces.StatusDAO;
import com.ewp.crm.service.interfaces.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class StatusServiceImpl implements StatusService {

	private final StatusDAO statusDAO;
	private final ClientDAO clientDAO;

	@Autowired
	public StatusServiceImpl(StatusDAO statusDAO, ClientDAO clientDAO) {
		this.statusDAO = statusDAO;
		this.clientDAO = clientDAO;
	}

	@Override
	public List<Status> getAll() {
		return statusDAO.findAll();
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
	public void add(Status status) {
		statusDAO.saveAndFlush(status);
	}

	@Override
	public void update(Status status) {
		checkStatusUnique(status);
		statusDAO.saveAndFlush(status);
	}

	private void checkStatusUnique(Status status) {
		Status statusFromDB = statusDAO.findStatusByName(status.getName());
		if (statusFromDB != null) {
			throw new StatusDataException("Статус с таким названием уже существует");
		}
	}

	@Override
	public void delete(Status status) {
		statusDAO.delete(status);
	}

	@Override
	public void delete(Long id) {
		Status statusFromDB = statusDAO.findOne(id);
		for (Client client : statusFromDB.getClients()) {
			client.setStatus(null);
		}
		statusFromDB.getClients().clear();
		statusDAO.saveAndFlush(statusFromDB);
		statusDAO.delete(statusFromDB);
	}

	@Override
	public void changeClientStatus(Long clientId, Long statusId) {
		Client client = clientDAO.findOne(clientId);
		Status beginStatus = statusDAO.findStatusByClientsIn(Collections.singletonList(client));
		Status endStatus = statusDAO.findOne(statusId);
		beginStatus.getClients().remove(client);
		statusDAO.saveAndFlush(beginStatus);
		endStatus.setClients(client);
		statusDAO.saveAndFlush(endStatus);
		client.setStatus(endStatus);
		clientDAO.saveAndFlush(client);
	}
}
