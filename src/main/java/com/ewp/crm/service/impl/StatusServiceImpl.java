package com.ewp.crm.service.impl;

import com.ewp.crm.exceptions.status.StatusExistsException;
import com.ewp.crm.models.Role;
import com.ewp.crm.models.SortedStatuses;
import com.ewp.crm.models.SortedStatuses.SortingType;
import com.ewp.crm.models.Status;
import com.ewp.crm.models.User;
import com.ewp.crm.models.dto.ClientDtoForBoard;
import com.ewp.crm.models.dto.StatusDtoForBoard;
import com.ewp.crm.repository.interfaces.ClientRepository;
import com.ewp.crm.repository.interfaces.SortedStatusesRepository;
import com.ewp.crm.repository.interfaces.StatusDAO;
import com.ewp.crm.repository.interfaces.UserDAO;
import com.ewp.crm.service.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class StatusServiceImpl implements StatusService {
	private final StatusDAO statusDAO;
	private ClientService clientService;
	private final ProjectPropertiesService propertiesService;
	private final SortedStatusesRepository sortedStatusesRepository;
	private final RoleService roleService;
	private final UserService userService;
	private final UserDAO userDAO;
	private final ClientRepository clientRepository;
	private Environment env;


	private static Logger logger = LoggerFactory.getLogger(StatusServiceImpl.class);

	@Autowired
	public StatusServiceImpl(StatusDAO statusDAO,
							 ProjectPropertiesService propertiesService,
							 SortedStatusesRepository sortedStatusesRepository,
							 RoleService roleService,
							 UserService userService,
							 UserDAO userDAO,
							 ClientRepository clientRepository,
							 Environment env) {
		this.statusDAO = statusDAO;
		this.propertiesService = propertiesService;
		this.sortedStatusesRepository = sortedStatusesRepository;
		this.roleService = roleService;
		this.userService = userService;
		this.userDAO = userDAO;
		this.clientRepository = clientRepository;
		this.env = env;
	}

	@Autowired
	private void setStatusService(ClientService clientService) {
		this.clientService = clientService;
	}

	//Для юзера из сессии смотрим для какого статуса какая нужна сортировка (и нужна ли)
	@Override
	public List<Status> getStatusesWithSortedClientsByRole(@AuthenticationPrincipal User userFromSession, Role role) {
		List<Status> statuses;
		List<Status> statusesWithSortedClients = new ArrayList<>();
		if(role.equals(roleService.getRoleByName("OWNER"))) {
			statuses = getAll();
		} else{
			statuses = getAllByRole(role);
		}
		SortedStatuses sorted;
		for (Status status : statuses) {
			sorted = new SortedStatuses(status, userFromSession);
			if (status.getSortedStatuses().size() != 0 && status.getSortedStatuses().contains(sorted)) {
				SortedStatuses finalSorted = sorted;
				SortingType sortingType = status.getSortedStatuses().stream().filter(data -> Objects.equals(data, finalSorted)).findFirst().get().getSortingType();
				Status newStatus = new Status();
				newStatus.setPosition(status.getPosition());
				newStatus.setClients(clientService.getOrderedClientsInStatus(status, sortingType, userFromSession));
				newStatus.setCreateStudent(status.isCreateStudent());
				newStatus.setName(status.getName());
				newStatus.setRole(status.getRole());
				newStatus.setInvisible(status.getInvisible());
				newStatus.setNextPaymentOffset(status.getNextPaymentOffset());
				newStatus.setTrialOffset(status.getTrialOffset());
				newStatus.setId(status.getId());
				newStatus.setSortedStatuses(status.getSortedStatuses());
				statusesWithSortedClients.add(newStatus);
			} else {
				statusesWithSortedClients.add(status);
			}
		}
		return statusesWithSortedClients;
	}

	@Override
	public List<Status> getAll() {
		return statusDAO.getAllByOrderByIdAsc();
	}

    @Override
    public List<Status> getAllByRole(Role role) {
        return statusDAO.getAllByRole(role);
    }

	@Override
	public Optional<Status> get(Long id) {
		return statusDAO.findById(id);
	}

	@Override
	public Optional<Status> get(String name) {
		return Optional.ofNullable(statusDAO.getStatusByName(name));
	}

	@Override
	public Optional<Status> getFirstStatusForClient() {
		return statusDAO.findById(propertiesService.getOrCreate().getNewClientStatus());
	}

	@Override
	public Optional<Status> getRepeatedStatusForClient() {
		return statusDAO.findById(propertiesService.getOrCreate().getRepeatedDefaultStatusId());
	}

	@Override
	public Optional<Status> getStatusByName(String name) {
		return Optional.ofNullable(statusDAO.getStatusByName(name));
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
			throw new StatusExistsException(env.getProperty("messaging.client.status.exception.allready-exist"));
		}
	}

	private void checkStatusId(Long id) {
		if (id == 1L) {
			throw new StatusExistsException(env.getProperty("messaging.client.status.exception.impossible-to-del-first"));
		}
		Optional<Status> optional = statusDAO.findById(id);
		Status statusFromDB = null;
		if (optional.isPresent()) {
			statusFromDB = optional.get();
		}
		if (statusFromDB.getName().equals("deleted")) {
			throw new StatusExistsException(env.getProperty("messaging.client.status.exception.deleted-impossible-to-del"));
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
	public Optional<Long> findMaxPosition() {
		return Optional.ofNullable(statusDAO.findMaxPosition());
	}

	@Override
	public List<Status> getAllStatusesForStudents() {
		return statusDAO.getAllStatusesForStudents();
	}

	@Override
	public void setNewOrderForChosenStatusForCurrentUser(SortingType newOrder, Long statusId, User currentUser) {
		if (get(statusId).isPresent()) {
			SortedStatuses sortedStatus = new SortedStatuses(get(statusId).get(), currentUser);
			sortedStatus.setSortingType(newOrder);
			sortedStatusesRepository.save(sortedStatus);
		}
	}

	@Override
	public List<StatusDtoForBoard> getStatusesDtoForBoardWithSortedClientsByRole(@AuthenticationPrincipal User userFromSession, Role role) {
		List<StatusDtoForBoard> statusDtoForBoards = new ArrayList<>();
		List<Long> stasusesId;
		if (role.equals(roleService.getRoleByName("OWNER"))) {
			stasusesId = statusDAO.getAllStatusesId();
		} else {
			stasusesId = statusDAO.getAllStatusByRole(role.getRoleName());
		}
		for (Long statusId : stasusesId) {
			List<Long> clientsId;
			List<ClientDtoForBoard> clientsDtoForBoards = new ArrayList<>();
			StatusDtoForBoard newStatusDTO = new StatusDtoForBoard();
			newStatusDTO.setId(statusId);
			newStatusDTO.setName(statusDAO.getStatusNameById(statusId));
			newStatusDTO.setInvisible(statusDAO.getStatusIsInvisibleById(statusId));
			newStatusDTO.setCreateStudent(statusDAO.getCreateStudentById(statusId));
			newStatusDTO.setPosition(statusDAO.getPositionById(statusId));
			newStatusDTO.setRole(statusDAO.getRoleById(statusId));
			newStatusDTO.setTrialOffset(statusDAO.getTrialOffsetById(statusId));
			newStatusDTO.setNextPaymentOffset(statusDAO.getNextPaymentOffsetById(statusId));
			//Достаем вообще есть ли тип по которому сравнивают это статус
			Optional<String> sortedStatusTypeName = sortedStatusesRepository.findSortedStatusTypeByStatusIdAndUserId(statusId, userFromSession.getId());
			if (sortedStatusTypeName.isPresent()) {
				clientsId = clientService.getOrderedClientsIdInStatus(statusId, sortedStatusTypeName.get(), userFromSession);
			} else {
				clientsId = clientService.getClientsIdFromStatus(statusId);
			}
			//Формируем клиентов
			for (Long clientId : clientsId) {
				ClientDtoForBoard newClientDTOForBoard = new ClientDtoForBoard(clientId);
				newClientDTOForBoard.setName(clientRepository.getClientNameById(clientId));
				newClientDTOForBoard.setLastName(clientRepository.getClientLastNameById(clientId));
				newClientDTOForBoard.setHideCard(clientRepository.getClientHideById(clientId));
				newClientDTOForBoard.setEmail(clientRepository.getClientFirstEmailById(clientId));
				newClientDTOForBoard.setPhone(clientRepository.getClientFirstPhoneById(clientId));
				BigInteger ownerId = clientRepository.getClientOwnerUserIdById(clientId);
				if (ownerId != null) {
					newClientDTOForBoard.setOwnerUser(userDAO.getById(ownerId.longValue()));
				}

				BigInteger mentorId = clientRepository.getClientOwnerMentorIdById(clientId);
				if (mentorId != null) {
					newClientDTOForBoard.setOwnerMentor(userDAO.getById(mentorId.longValue()));
				}
				clientsDtoForBoards.add(newClientDTOForBoard);
			}
			newStatusDTO.setClients(clientsDtoForBoards);
			statusDtoForBoards.add(newStatusDTO);
		}
		return statusDtoForBoards;
	}
}
