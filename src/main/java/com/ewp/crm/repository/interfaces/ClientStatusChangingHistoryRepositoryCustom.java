package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Status;
import com.ewp.crm.models.dto.ClientDto;

import java.time.ZonedDateTime;
import java.util.List;

public interface ClientStatusChangingHistoryRepositoryCustom {

    List<ClientDto> getNewClientsInPeriodButExcludeStatuses(ZonedDateTime beginDate, ZonedDateTime endDate, Status... excludeStatuses);

    List<ClientDto> getNewClientsInStatusAndPeriodButExcludeStatuses(Status status, ZonedDateTime beginDate, ZonedDateTime endDate, Status... excludeStatuses);

    List<ClientDto> getClientsEverBeenInStatusButExcludeStatuses(Status status, Status... excludeStatuses);

    List<ClientDto> getClientsBeenInStatusAtPeriodButExcludeStatuses(Status status, ZonedDateTime beginDate, ZonedDateTime endDate, Status... excludeStatuses);

    List<ClientDto> getClientsBeenInStatusFirstTimeAtPeriodButExcludeStatuses(Status status, ZonedDateTime beginDate, ZonedDateTime endDate, Status... excludeStatuses);

    void markAllFakeStatusesByChangingInIntervalRule(int minutes);

    void markAllFakeStatusesByReturningInIntervalRule(int hours);

    List<ClientDto> getClientsWhoChangedStatusInPeriodButExcludeStatuses(Status sourceStatus, Status destinationStatus, ZonedDateTime beginDate, ZonedDateTime endDate, Status... excludeStatuses);

    void setCreationInNearestStatusChangingHistoryForClient(Client client, ZonedDateTime date);

}
