package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.SortedStatuses;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SortedStatusesRepository extends CommonGenericRepository<SortedStatuses> {
    @Query(value = "SELECT sorting_type FROM sorted_statuses where status_status_id = ?1 and user_user_id = ?2", nativeQuery = true)
    Optional<String> findSortedStatusTypeByStatusIdAndUserId(Long statusId, Long userId);
}
