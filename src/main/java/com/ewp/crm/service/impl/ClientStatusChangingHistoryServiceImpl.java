package com.ewp.crm.service.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientStatusChangingHistory;
import com.ewp.crm.repository.interfaces.ClientStatusChangingHistoryRepository;
import com.ewp.crm.service.interfaces.ClientStatusChangingHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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



}
