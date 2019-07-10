package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientStatusChangingHistory;
import com.ewp.crm.models.Status;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface ClientStatusChangingHistoryService extends CommonService<ClientStatusChangingHistory> {

    List<ClientStatusChangingHistory> getAllClientStatusChangingHistoryByClient(Client client);

    List<ClientStatusChangingHistory> getAllClientStatusChangingHistoryByClientByDate(Client client);

    Optional<ClientStatusChangingHistory> getFirstClientStatusChangingHistoryByClient(Client client);

    Optional<ClientStatusChangingHistory> getLastClientStatusChangingHistoryByClient(Client client);

    List<Client> getClientsEverBeenInStatusButExcludeStatuses(Status status, Status... excludeStatuses);

    List<Client> getClientsBeenInStatusAtPeriodButExcludeStatuses(Status status, ZonedDateTime beginDate, ZonedDateTime endDate, Status... excludeStatuses);

    List<Client> getClientsWhoChangedStatusInPeriodButExcludeStatuses(Status sourceStatus, Status destinationStatus, ZonedDateTime beginDate, ZonedDateTime endDate, Status... excludeStatuses);

    void markAllFakeStatusesByChangingInIntervalRule(int minutes);

    void markAllFakeStatusesByReturningInIntervalRule(int hours);

}
