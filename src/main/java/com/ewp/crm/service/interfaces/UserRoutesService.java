package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.UserRoutes;
import com.ewp.crm.models.dto.UserRoutesDto;

import java.util.List;
import java.util.Set;

public interface UserRoutesService {
    Set<UserRoutes> getByUserId(Long userId);
    UserRoutes getByUserIdAndUserRouteType(Long userId, UserRoutes.UserRouteType type);
    List<UserRoutes> getAllByUserRouteType(UserRoutes.UserRouteType userRouteType);
    void save(UserRoutes userRoutes);
    void saveAll(Set<UserRoutes> userRoutes);
    List<UserRoutesDto>  getUserByRoleAndUserRoutesType(String userRole, String userRouteType);
    Long getUserIdByPercentChance(List<UserRoutesDto> userRoutesList);
    void updateUserRoutes(List<UserRoutesDto> userRoutesDtoListist);
}
