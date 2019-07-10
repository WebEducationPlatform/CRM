package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Status;

import java.time.ZonedDateTime;
import java.util.List;

public interface ClientStatusChangingHistoryRepositoryCustom {

    List<Client> getClientsEverBeenInStatusButExcludeStatuses(Status status, Status... excludeStatuses);

    List<Client> getClientsBeenInStatusAtPeriodButExcludeStatuses(Status status, ZonedDateTime beginDate, ZonedDateTime endDate, Status... excludeStatuses);

    void markAllFakeStatusesByChangingInIntervalRule(int minutes);

    void markAllFakeStatusesByReturningInIntervalRule(int hours);

    List<Client> getClientsWhoChangedStatusInPeriodButExcludeStatuses(Status sourceStatus, Status destinationStatus, ZonedDateTime beginDate, ZonedDateTime endDate, Status... excludeStatuses);

}
