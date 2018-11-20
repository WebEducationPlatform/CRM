package com.ewp.crm.service.impl;

import com.ewp.crm.exceptions.status.StatusExistsException;
import com.ewp.crm.models.Status;
import com.ewp.crm.models.User;
import com.ewp.crm.repository.interfaces.StatusDAO;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.ProjectPropertiesService;
import com.ewp.crm.service.interfaces.StatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StatusServiceImpl implements StatusService {
	private final StatusDAO statusDAO;
	private final ClientService clientService;
	private final ProjectPropertiesService propertiesService;

	private static Logger logger = LoggerFactory.getLogger(StatusServiceImpl.class);

	@Autowired
	public StatusServiceImpl(StatusDAO statusDAO, ClientService clientService, ProjectPropertiesService propertiesService) {
		this.statusDAO = statusDAO;
		this.clientService = clientService;
		this.propertiesService = propertiesService;
	}

	@Override
	public List<Status> getAll() {
		return statusDAO.getAllByOrderByIdAsc();
	}

	@Override
	public List<Status> getStatusesWithClientsForUser(User ownerUser) {
		List<Status> statuses = getAll();
		for (Status status : statuses) {
			status.setClients(clientService.getClientsByStatusAndOwnerUserOrOwnerUserIsNull(status, ownerUser));
		}
		return statuses;
	}

	@Override
	public Status get(Long id) {
		Optional<Status> optional = statusDAO.findById(id);
		return optional.orElse(null);
	}

	@Override
	public Status get(String name) {
		return statusDAO.getStatusByName(name);
	}

	@Override
	public Status getFirstStatusForClient() {
		Optional<Status> optional = statusDAO.findById(propertiesService.getOrCreate().getNewClientStatus());
		return optional.orElse(null);
	}

	@Override
	public Status getStatusByName(String name) {
		Status statusByName = statusDAO.getStatusByName(name);
		assert statusByName!=null;
		return statusByName;
	}

	@Override
	public void add(Status status) {
		logger.info("{} adding of a new status...", StatusServiceImpl.class.getName());
		checkStatusUnique(status);
		Long position = statusDAO.findMaxPosition() + 1L;
		status.setPosition(position);
		statusDAO.saveAndFlush(status);
		logger.info("{} status added successfully...", StatusServiceImpl.class.getName());
	}

	@Override
	public void addInit(Status status) {
		checkStatusUnique(status);
		logger.info("{} adding of a new status...", StatusServiceImpl.class.getName());
		statusDAO.saveAndFlush(status);
		logger.info("{} status added successfully...", StatusServiceImpl.class.getName());
	}

	@Override
	public void update(Status status) {
		logger.info("{} updating of the status...", StatusServiceImpl.class.getName());
		checkStatusUnique(status);
		statusDAO.saveAndFlush(status);
		logger.info("{} status updated successfully...", StatusServiceImpl.class.getName());
	}

	private void checkStatusUnique(Status status) {
		Status statusFromDB = statusDAO.getStatusByName(status.getName());
		if (statusFromDB != null && !statusFromDB.equals(status)) {
			throw new StatusExistsException("Статус с таким названием уже существует");
		}
	}

	private void checkStatusId(Long id) {
		if (id == 1L) {
			throw new StatusExistsException("Статус с индексом \"1\" нельзя удалить");
		}
		Optional<Status> optional = statusDAO.findById(id);
		Status statusFromDB = null;
		if (optional.isPresent()) {
			statusFromDB = optional.get();
		}
		if (statusFromDB.getName().equals("deleted")) {
			throw new StatusExistsException("Статус deleted нельзя удалить");
		}
	}

	private void transferStatusClientsBeforeDelete(Status status) {
		if (status.getClients() != null) {
			Status defaultStatus = statusDAO.getStatusByName("deleted");
			defaultStatus.getClients().addAll(status.getClients());
			status.getClients().clear();
			statusDAO.saveAndFlush(status);
		}
	}

	@Override
	public void delete(Status status) {
		logger.info("{} deleting of the status...", StatusServiceImpl.class.getName());
		delete(status.getId());
		logger.info("{} status deleted successfully...", StatusServiceImpl.class.getName());
	}

	@Override
	public void delete(Long id) {
		logger.info("{} deleting of the status...", StatusServiceImpl.class.getName());
		checkStatusId(id);
		Optional<Status> optional = statusDAO.findById(id);
		Status statusFromDB = null;
		if (optional.isPresent()) {
			statusFromDB = optional.get();
		}
		transferStatusClientsBeforeDelete(statusFromDB);
		statusDAO.delete(statusFromDB);
		logger.info("{} status deleted successfully...", StatusServiceImpl.class.getName());
	}

	@Override
	public Long findMaxPosition() {
		return statusDAO.findMaxPosition();
	}

	@Override
	public List<Status> getAllStatusesForStudents() {
		return statusDAO.getAllStatusesForStudents();
	}
}
