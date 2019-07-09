package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.SortedStatuses;
import com.ewp.crm.models.SortedStatusesId;

public interface SortedStatusesRepository extends CommonGenericRepository<SortedStatuses> {

    SortedStatuses getSortedStatusesBySortedStatusesId(SortedStatusesId sortedStatusesId);

}
