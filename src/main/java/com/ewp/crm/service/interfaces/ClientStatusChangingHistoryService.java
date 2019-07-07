package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientStatusChangingHistory;

import java.util.List;
import java.util.Optional;

public interface ClientStatusChangingHistoryService extends CommonService<ClientStatusChangingHistory> {

    List<ClientStatusChangingHistory> getAllClientStatusChangingHistoryByClient(Client client);

    List<ClientStatusChangingHistory> getAllClientStatusChangingHistoryByClientByDate(Client client);

    Optional<ClientStatusChangingHistory> getFirstClientStatusChangingHistoryByClient(Client client);

    Optional<ClientStatusChangingHistory> getLastClientStatusChangingHistoryByClient(Client client);

}
