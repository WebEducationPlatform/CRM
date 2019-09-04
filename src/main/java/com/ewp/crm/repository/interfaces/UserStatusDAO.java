package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Role;
import com.ewp.crm.models.Status;
import com.ewp.crm.models.User;
import com.ewp.crm.models.UserStatus;
import com.ewp.crm.models.dto.StatusDtoForBoard;
import com.ewp.crm.models.dto.StatusPositionIdNameDTO;

import java.util.List;

public interface UserStatusDAO {

    void addStatusAllUsers(Status status);

    void addUserAllStatus(User user);

    void deleteStatus(Long status_id);

    void deleteUser(Long user_id);

    void updateUserStatus(Status status, User user);

    UserStatus getStatus(Long status_id, Long user_id);

    List<StatusPositionIdNameDTO> getAllStatusVisibleTrue(Long user_id);

    List<StatusDtoForBoard> getStatusesForBoard(Long user_id, List<Role> roleList);

    List<UserStatus> getAllUserStatus();
}
