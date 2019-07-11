package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientStatusChangingHistory;
import com.ewp.crm.models.Status;
import com.ewp.crm.models.dto.ClientDto;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface ClientStatusChangingHistoryService extends CommonService<ClientStatusChangingHistory> {

    List<ClientStatusChangingHistory> getAllClientStatusChangingHistoryByClient(Client client);

    List<ClientStatusChangingHistory> getAllClientStatusChangingHistoryByClientByDate(Client client);

    Optional<ClientStatusChangingHistory> getFirstClientStatusChangingHistoryByClient(Client client);

    Optional<ClientStatusChangingHistory> getLastClientStatusChangingHistoryByClient(Client client);

    List<ClientDto> getClientsEverBeenInStatusButExcludeStatuses(Status status, Status... excludeStatuses);

    List<ClientDto> getClientsBeenInStatusAtPeriodButExcludeStatuses(Status status, ZonedDateTime beginDate, ZonedDateTime endDate, Status... excludeStatuses);

    List<ClientDto> getClientsWhoChangedStatusInPeriodButExcludeStatuses(Status sourceStatus, Status destinationStatus, ZonedDateTime beginDate, ZonedDateTime endDate, Status... excludeStatuses);

    List<ClientDto> getClientsBeenInStatusFirstTimeAtPeriodButExcludeStatuses(Status status, ZonedDateTime beginDate, ZonedDateTime endDate, Status... excludeStatuses);

    List<ClientDto> getNewClientsInPeriodButExcludeStatuses(ZonedDateTime beginDate, ZonedDateTime endDate, Status... excludeStatuses);

    List<ClientDto> getNewClientsInStatusAndPeriodButExcludeStatuses(Status status, ZonedDateTime beginDate, ZonedDateTime endDate, Status... excludeStatuses);

    void markAllFakeStatusesByChangingInIntervalRule(int minutes);

    void markAllFakeStatusesByReturningInIntervalRule(int hours);

    void setCreationInNearestStatusChangingHistoryForClient(Client client, ZonedDateTime date);

}
