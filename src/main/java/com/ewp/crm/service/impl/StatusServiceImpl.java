package com.ewp.crm.service.impl;

import com.ewp.crm.exceptions.status.StatusExistsException;
import com.ewp.crm.models.Status;
import com.ewp.crm.models.User;
import com.ewp.crm.repository.interfaces.StatusDAO;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatusServiceImpl implements StatusService {
	private final StatusDAO statusDAO;
	private final ClientService clientService;

	@Autowired
	public StatusServiceImpl(StatusDAO statusDAO, ClientService clientService) {
		this.statusDAO = statusDAO;
		this.clientService = clientService;
	}

	@Override
	public List<Status> getAll() {
		return statusDAO.findAllByOrderByIdAsc();
	}

	@Override
	public List<Status> getStatusesWithClientsForUser(User ownerUser) {
		List<Status> statuses = getAll();
		for (Status status : statuses) {
			status.setClients(clientService.findByStatusAndOwnerUserOrOwnerUserIsNull(status, ownerUser));
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
		return statusDAO.findOne(1L);
	}

	@Override
	public void add(Status status) {
		checkStatusUnique(status);
		Long position = statusDAO.findMaxPosition() + 1L;
		status.setPosition(position);
		statusDAO.saveAndFlush(status);
	}

	@Override
	public void addInit(Status status) {
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
		if (statusDAO.findOne(id).getName().equals("deleted")) {
			throw new StatusExistsException("Статус deleted нельзя удалить");
		}
	}

	private void transferStatusClientsBeforeDelete(Status status) {
		if (status.getClients() != null) {
			Status defaultStatus = statusDAO.findStatusByName("deleted");
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
	public Long findMaxPosition() {
		return statusDAO.findMaxPosition();
	}
}
