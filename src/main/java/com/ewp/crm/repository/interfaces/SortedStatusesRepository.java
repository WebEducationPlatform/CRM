package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.SortedStatuses;
import com.ewp.crm.models.Status;
import com.ewp.crm.models.User;

import java.util.Optional;

public interface SortedStatusesRepository extends CommonGenericRepository<SortedStatuses> {

    Optional<SortedStatuses> findSortedStatusesByStatusAndUser(Status status, User user);

}
