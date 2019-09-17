package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.UserStatus;

import java.util.List;

public interface UserStatusDAO {

    void addStatusForAllUsers(Long status_id);

    void addUserForAllStatuses(Long user_id);

    void deleteStatus(Long status_id);

    void deleteUser(Long user_id);

    void updateUserStatus(Long user_id, Long status_id, boolean is_invisible, Long position);

    UserStatus getUserStatus(Long user_id, Long status_id);

    List<UserStatus> getStatusByUserId(Long user_id);

    List<UserStatus> getUserByStatusId(Long status_id);

    List<UserStatus> getAll();

    void addStatusForUser(Long user_id, Long status_id, boolean is_invisible, Long position);
}
