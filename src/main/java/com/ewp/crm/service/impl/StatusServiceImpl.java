package com.ewp.crm.service.impl;

import com.ewp.crm.exceptions.status.StatusExistsException;
import com.ewp.crm.models.*;
import com.ewp.crm.models.SortedStatuses.SortingType;
import com.ewp.crm.models.dto.StatusPositionIdNameDTO;
import com.ewp.crm.repository.interfaces.SortedStatusesRepository;
import com.ewp.crm.repository.interfaces.StatusRepository;
import com.ewp.crm.service.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@Transactional
public class StatusServiceImpl implements StatusService {
    private final StatusRepository statusDAO;
    private ClientService clientService;
    private final ProjectPropertiesService propertiesService;
    private final SortedStatusesRepository sortedStatusesRepository;
    private final RoleService roleService;
    private final UserService userService;
    private final ClientStatusChangingHistoryService clientStatusChangingHistoryService;
    private Environment env;


    private static Logger logger = LoggerFactory.getLogger(StatusServiceImpl.class);

    @Autowired
    public StatusServiceImpl(StatusRepository statusDAO, ProjectPropertiesService propertiesService,
                             SortedStatusesRepository sortedStatusesRepository, RoleService roleService,
                             Environment env, UserService userService, ClientStatusChangingHistoryService clientStatusChangingHistoryService) {
        this.statusDAO = statusDAO;
        this.propertiesService = propertiesService;
        this.sortedStatusesRepository = sortedStatusesRepository;
        this.roleService = roleService;
        this.env = env;
        this.userService = userService;
        this.clientStatusChangingHistoryService = clientStatusChangingHistoryService;
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
        List<Role> roles = Collections.singletonList(roleService.getRoleByName("OWNER"));
        add(status, roles);
    }

    @Override
    public void add(Status status, List<Role> roles) {
        logger.info("{} adding of a new status...", StatusServiceImpl.class.getName());
        checkStatusUnique(status);
        Long position = statusDAO.findMaxPosition() + 1L;
        status.setRole(new ArrayList<>(roles));
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
        User user = userService.get(1L);
        if (status.getClients() != null) {
            Status defaultStatus = statusDAO.getStatusByName("deleted");
            defaultStatus.getClients().addAll(status.getClients());
            for (Client client : status.getClients()) {
                ClientStatusChangingHistory clientStatusChangingHistory = new ClientStatusChangingHistory(ZonedDateTime.now(), status, defaultStatus, client, user);
                clientStatusChangingHistoryService.add(clientStatusChangingHistory);
            }
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
    public Optional<SortingType> findOrderForChosenStatusForCurrentUser(Long statusId, User userFromSession) {
        if (get(statusId).isPresent()) {
            Optional<SortedStatuses> optional = sortedStatusesRepository.findSortedStatusesByStatusAndUser(get(statusId).get(), userFromSession);
            if (optional.isPresent()) {
                return Optional.of(optional.get().getSortingType());
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Status> getAllStatusesForStudents() {
        return statusDAO.getAllStatusesForStudents();
    }

    @Override
    @Transactional
    public void setNewOrderForChosenStatusForCurrentUser(SortingType newOrder, Long statusId, User currentUser) {
        Optional<Status> optionalStatus = get(statusId);
        if (optionalStatus.isPresent()) {
            SortedStatuses sortedStatus = new SortedStatuses(optionalStatus.get(), currentUser);
            sortedStatus.setSortingType(newOrder);
            sortedStatusesRepository.save(sortedStatus);
        }
    }

    @Override
    public List<StatusPositionIdNameDTO> getAllStatusesMinDTOWhichAreNotInvisible() {
        List<BigInteger> ids = statusDAO.getAllIdsWhichNotInvisible();
        List<StatusPositionIdNameDTO> statusPositionIdNameDTOS = new ArrayList<>();
        for (BigInteger id : ids) {
            statusPositionIdNameDTOS.add(new StatusPositionIdNameDTO(id.longValue(), statusDAO.getStatusPositionById(id.longValue()), statusDAO.getStatusNameById(id.longValue())));
        }
        return statusPositionIdNameDTOS;
    }

    //Для юзера из сессии смотрим для какого статуса какая нужна сортировка (и нужна ли)
    @Override
    public List<Status> getStatusesWithSortedClientsByRole(@AuthenticationPrincipal User userFromSession, Role role) {
        List<Status> statuses;
        List<Status> statusesWithSortedClients = new ArrayList<>();
        if (role.equals(roleService.getRoleByName("OWNER"))) {
            statuses = getAll();
        } else {
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
                newStatus.setClients(clientService.getOrderedClientsInStatus(status, sortingType));
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
    public Optional<Status> findStatusWithSortedClientsByUser(Long statusId, User userFromSession) {
        final Optional<Status> optionalStatus = get(statusId);
        if (!(optionalStatus.isPresent())) {
            return Optional.empty();
        }

        final Status status = optionalStatus.get();

        Optional<SortedStatuses> optionalSortedStatuses = sortedStatusesRepository.findSortedStatusesByStatusAndUser(status,userFromSession);
        final SortingType sortingType = optionalSortedStatuses.isPresent()
                ? optionalSortedStatuses.get().getSortingType()
                : SortingType.NEW_FIRST;

        final List<Client> sortedClients = clientService.getSortedClientsByStatus(status, sortingType);

        final Status newStatus = new Status();
        newStatus.setClients(sortedClients);
        newStatus.setPosition(status.getPosition());
        newStatus.setCreateStudent(status.isCreateStudent());
        newStatus.setName(status.getName());
        newStatus.setRole(status.getRole());
        newStatus.setInvisible(status.getInvisible());
        newStatus.setNextPaymentOffset(status.getNextPaymentOffset());
        newStatus.setTrialOffset(status.getTrialOffset());
        newStatus.setId(status.getId());
        newStatus.setSortedStatuses(status.getSortedStatuses());

        return Optional.of(newStatus);
    }

}