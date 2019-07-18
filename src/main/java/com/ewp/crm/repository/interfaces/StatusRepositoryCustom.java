package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Role;
import com.ewp.crm.models.dto.StatusDtoForBoard;

import java.util.List;

public interface StatusRepositoryCustom {

    List<StatusDtoForBoard> getStatusesForBoard(long userId, List<Role> roles, long roleId);

}
