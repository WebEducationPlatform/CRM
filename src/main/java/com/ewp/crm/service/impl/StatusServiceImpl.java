package com.ewp.crm.service.impl;

import com.ewp.crm.exceptions.status.StatusExistsException;
import com.ewp.crm.models.*;
import com.ewp.crm.repository.interfaces.SortedStatusesRepository;
import com.ewp.crm.repository.interfaces.StatusDAO;
import com.ewp.crm.service.interfaces.ClientHistoryService;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.ProjectPropertiesService;
import com.ewp.crm.service.interfaces.StatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;

@Service
public class StatusServiceImpl implements StatusService {
	private final StatusDAO statusDAO;
	private ClientService clientService;
	private final ProjectPropertiesService propertiesService;
	private final ClientHistoryService clientHistoryService;
	private final SortedStatusesRepository sortedStatusesRepository;

	private static Logger logger = LoggerFactory.getLogger(StatusServiceImpl.class);

	@Autowired
	public StatusServiceImpl(StatusDAO statusDAO,
							 ProjectPropertiesService propertiesService,
							 ClientHistoryService clientHistoryService,
							 SortedStatusesRepository sortedStatusesRepository) {
		this.statusDAO = statusDAO;
		this.propertiesService = propertiesService;
        this.clientHistoryService = clientHistoryService;
		this.sortedStatusesRepository = sortedStatusesRepository;
	}

	@Autowired
	private void setStatusService(ClientService clientService) {
		this.clientService = clientService;
	}

	@Override
	public List<Status> getAll() {
		return statusDAO.getAllByOrderByIdAsc();
	}

    @Override
    public List<Status> getStatusesWithSortedClients(@AuthenticationPrincipal User userFromSession) {
        List<Status> statuses = getAll();
        SortedStatuses sorted;
        for (Status status : statuses) {
            sorted = new SortedStatuses(status, userFromSession);
            if (status.getSortedStatuses().size() != 0 && status.getSortedStatuses().contains(sorted)) {
                SortedStatuses finalSorted = sorted;
                String sortingType = status.getSortedStatuses().stream().filter(data -> Objects.equals(data, finalSorted)).findFirst().get().getSortingType();
                //System.out.println(status.getName() + " " + sortingType);
                status.setClients(sortClients(status.getClients(), sortingType));
            }
        }
        return statuses;
    }

    private List<Client> sortClients(List<Client> clients, String sortingType) {
        if ("oldFirst".equals(sortingType)) {
			//clients.sort(Comparator.comparing(Client::getDateOfRegistration));
            return clients;
        }
        if ("newFirst".equals(sortingType)) {
            clients.sort(Comparator.comparing(Client::getDateOfRegistration).reversed());
            return clients;
        }
        if ("oldChangesFirst".equals(sortingType)) {
            clients.sort((client1, client2) -> {
                ZonedDateTime lastChangesClient1 = clientHistoryService.getLastClientChangesDate(client1);
                ZonedDateTime lastChangesClient2 = clientHistoryService.getLastClientChangesDate(client2);
                return lastChangesClient1.compareTo(lastChangesClient2);
            });
            return clients;
        }
        if ("newChangesFirst".equals(sortingType)) {
            clients.sort((client1, client2) -> {
                ZonedDateTime lastChangesClient1 = clientHistoryService.getLastClientChangesDate(client1);
                ZonedDateTime lastChangesClient2 = clientHistoryService.getLastClientChangesDate(client2);
                return lastChangesClient2.compareTo(lastChangesClient1);
            });
            return clients;
        }
        return clients;
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
	public Status getRepeatedStatusForClient() {
		Optional<Status> optional = statusDAO.findById(propertiesService.getOrCreate().getRepeatedDefaultStatusId());
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

	@Override
	public void setNewOrderForChosenStatusForCurrentUser(String newOrder, Long statusId, User currentUser) {
		SortedStatuses sortedStatus = new SortedStatuses(get(statusId), currentUser);
		sortedStatus.setSortingType(newOrder);
		sortedStatusesRepository.save(sortedStatus);
	}
}
