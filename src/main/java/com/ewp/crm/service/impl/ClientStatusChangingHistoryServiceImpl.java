package com.ewp.crm.service.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientStatusChangingHistory;
import com.ewp.crm.models.Status;
import com.ewp.crm.models.dto.ClientDto;
import com.ewp.crm.repository.interfaces.ClientStatusChangingHistoryRepository;
import com.ewp.crm.service.interfaces.ClientStatusChangingHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ClientStatusChangingHistoryServiceImpl extends CommonServiceImpl<ClientStatusChangingHistory> implements ClientStatusChangingHistoryService {

    private static Logger logger = LoggerFactory.getLogger(ClientStatusChangingHistoryServiceImpl.class);

    private final ClientStatusChangingHistoryRepository clientStatusChangingHistoryRepository;

    @Autowired
    public ClientStatusChangingHistoryServiceImpl(ClientStatusChangingHistoryRepository clientStatusChangingHistoryRepository) {
        this.clientStatusChangingHistoryRepository = clientStatusChangingHistoryRepository;
    }

    @Override
    public List<ClientStatusChangingHistory> getAllClientStatusChangingHistoryByClient(Client client) {
        return clientStatusChangingHistoryRepository.findAllByClientId(client.getId());
    }

    @Override
    public List<ClientStatusChangingHistory> getAllClientStatusChangingHistoryByClientByDate(Client client) {
        return clientStatusChangingHistoryRepository.findAllByClientIdOrderByDateAsc(client.getId());
    }

    @Override
    public Optional<ClientStatusChangingHistory> getFirstClientStatusChangingHistoryByClient(Client client) {
        return Optional.ofNullable(clientStatusChangingHistoryRepository.getFirstByClientId(client.getId()));
    }

    @Override
    public Optional<ClientStatusChangingHistory> getLastClientStatusChangingHistoryByClient(Client client) {
        return Optional.ofNullable(clientStatusChangingHistoryRepository.getTopByClientId(client.getId()));
    }

    @Override
    public List<ClientDto> getClientsEverBeenInStatusButExcludeStatuses(Status status, Status... excludeStatuses) {
        return clientStatusChangingHistoryRepository.getClientsEverBeenInStatusButExcludeStatuses(status, excludeStatuses);
    }

    @Override
    public List<ClientDto> getClientsBeenInStatusAtPeriodButExcludeStatuses(Status status, ZonedDateTime beginDate, ZonedDateTime endDate, Status... excludeStatuses) {
        return clientStatusChangingHistoryRepository.getClientsBeenInStatusAtPeriodButExcludeStatuses(status, beginDate, endDate, excludeStatuses);
    }

    @Override
    public List<ClientDto> getClientsWhoChangedStatusInPeriodButExcludeStatuses(Status sourceStatus, Status destinationStatus, ZonedDateTime beginDate, ZonedDateTime endDate, Status... excludeStatuses) {
        return clientStatusChangingHistoryRepository.getClientsWhoChangedStatusInPeriodButExcludeStatuses(sourceStatus, destinationStatus, beginDate, endDate, excludeStatuses);
    }

    @Override
    public void markAllFakeStatusesByChangingInIntervalRule(int minutes) {
        clientStatusChangingHistoryRepository.markAllFakeStatusesByChangingInIntervalRule(minutes);
    }

    @Override
    public void markAllFakeStatusesByReturningInIntervalRule(int hours) {
        clientStatusChangingHistoryRepository.markAllFakeStatusesByReturningInIntervalRule(hours);
    }

    @Override
    public List<ClientDto> getClientsBeenInStatusFirstTimeAtPeriodButExcludeStatuses(Status status, ZonedDateTime beginDate, ZonedDateTime endDate, Status... excludeStatuses) {
        return clientStatusChangingHistoryRepository.getClientsBeenInStatusFirstTimeAtPeriodButExcludeStatuses(status, beginDate, endDate, excludeStatuses);
    }

    @Override
    public void setCreationInNearestStatusChangingHistoryForClient(Client client, ZonedDateTime date) {
        clientStatusChangingHistoryRepository.setCreationInNearestStatusChangingHistoryForClient(client, date);
    }

    @Override
    public List<ClientDto> getNewClientsInPeriodButExcludeStatuses(ZonedDateTime beginDate, ZonedDateTime endDate, Status... excludeStatuses) {
        return clientStatusChangingHistoryRepository.getNewClientsInPeriodButExcludeStatuses(beginDate, endDate, excludeStatuses);
    }

    @Override
    public List<ClientDto> getNewClientsInStatusAndPeriodButExcludeStatuses(Status status, ZonedDateTime beginDate, ZonedDateTime endDate, Status... excludeStatuses) {
        return clientStatusChangingHistoryRepository.getNewClientsInStatusAndPeriodButExcludeStatuses(status, beginDate, endDate, excludeStatuses);
    }

}
