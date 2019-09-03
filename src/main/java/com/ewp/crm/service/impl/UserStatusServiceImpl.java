package com.ewp.crm.service.impl;

import com.ewp.crm.models.Role;
import com.ewp.crm.models.Status;
import com.ewp.crm.models.User;
import com.ewp.crm.models.UserStatus;
import com.ewp.crm.models.dto.StatusDtoForBoard;
import com.ewp.crm.models.dto.StatusPositionIdNameDTO;
import com.ewp.crm.repository.interfaces.UserStatusDAO;
import com.ewp.crm.service.interfaces.UserStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserStatusServiceImpl implements UserStatusService {

    @Autowired
    UserStatusDAO userStatusDAO;

    @Override
    public void addStatusAllUsers(Status status) {
        userStatusDAO.addStatusAllUsers(status);
    }

    @Override
    public void addUserAllStatus(User user) {
        userStatusDAO.addUserAllStatus(user);
    }

    @Override
    public void deleteStatus(Long status_id) {
        userStatusDAO.deleteStatus(status_id);
    }

    @Override
    public void deleteUser(Long user_id) {
        userStatusDAO.deleteUser(user_id);
    }

    @Override
    public void updateUserStatus(Status status, User user) {
        userStatusDAO.updateUserStatus(status, user);
    }

    @Override
    public UserStatus getStatus(Long status_id, Long user_id) {
        return userStatusDAO.getStatus(status_id, user_id);
    }

    @Override
    public List<StatusPositionIdNameDTO> getAllStatusVisibleTrue(Long user_id) {
        return userStatusDAO.getAllStatusVisibleTrue(user_id);
    }

    @Override
    public List<StatusDtoForBoard> getStatusesForBoard(Long user_id, List<Role> roleList) {
        return userStatusDAO.getStatusesForBoard(user_id, roleList);
    }

    @Override
    public List<UserStatus> getAllUserStatus() {
        return userStatusDAO.getAllUserStatus();
    }
}
