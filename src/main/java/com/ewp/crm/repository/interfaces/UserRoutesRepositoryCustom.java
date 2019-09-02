package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.dto.UserRoutesDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserRoutesRepositoryCustom {
    @Transactional
    List<UserRoutesDto> getUserByRoleUserRoutesType(String userRole, String userRouteType);
}
