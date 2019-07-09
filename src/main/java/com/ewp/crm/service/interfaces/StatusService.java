package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Role;
import com.ewp.crm.models.SortedStatuses;
import com.ewp.crm.models.SortedStatuses.SortingType;
import com.ewp.crm.models.SortedStatusesId;
import com.ewp.crm.models.Status;
import com.ewp.crm.models.User;
import com.ewp.crm.models.dto.StatusPositionIdNameDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;
import java.util.Optional;

public interface StatusService {

    List<Status> getAll();

    List<Status> getStatusesWithSortedClientsByRole(@AuthenticationPrincipal User userFromSession, Role role);

    List<Status> getAllByRole(Role role);

    Optional<Status> get(Long id);

    Optional<Status> get(String name);

    Optional<Status> getFirstStatusForClient();

    Optional<Status> getRepeatedStatusForClient();

    Optional<Status> getStatusByName(String name);

	void add(Status status);

    void addInit(Status status);

    void update(Status status);

    void delete(Status status);

    void delete(Long id);

    Optional<Long> findMaxPosition();

    List<Status> getAllStatusesForStudents();

    void setNewOrderForChosenStatusForCurrentUser(SortingType newOrder, Long statusId, User currentUser);

    List<StatusPositionIdNameDTO> getAllStatusesMinDTOWhichAreNotInvisible();

    SortedStatuses getBySortedStatusesId(SortedStatusesId sortedStatusesId);

}
