package com.ewp.crm.service.impl;

import com.ewp.crm.models.UserStatus;
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
    public void addStatusForAllUsers(Long status_id) {
        userStatusDAO.addStatusForAllUsers(status_id);
    }

    @Override
    public void addUserForAllStatuses(Long user_id) {
        userStatusDAO.addUserForAllStatuses(user_id);
    }

    @Override
    public void addStatusForUser(Long user_id, Long status_id, boolean is_invisible, Long position) {
        userStatusDAO.addStatusForUser(user_id, status_id, is_invisible, position);
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
    public void updateUserStatus(Long user_id, Long status_id, boolean is_invisible, Long position) {
        userStatusDAO.updateUserStatus(user_id, status_id, is_invisible, position);
    }

    @Override
    public UserStatus getUserStatus(Long user_id, Long status_id) {
        return userStatusDAO.getUserStatus(user_id, status_id);
    }

    @Override
    public List<UserStatus> getStatusByUserId(Long user_id) {
        return userStatusDAO.getStatusByUserId(user_id);
    }

    @Override
    public List<UserStatus> getUserByStatusId(Long status_id) {
        return userStatusDAO.getUserByStatusId(status_id);
    }

    @Override
    public List<UserStatus> getAll() {
        return userStatusDAO.getAll();
    }

    @Override
    public void updateUserStatusNotifications(Long user_id, Long status_id, boolean send_notifications){
        userStatusDAO.updateUserStatusNotifications(user_id, status_id, send_notifications);
    }

}
