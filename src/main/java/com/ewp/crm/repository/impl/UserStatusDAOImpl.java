package com.ewp.crm.repository.impl;

import com.ewp.crm.models.UserStatus;
import com.ewp.crm.repository.interfaces.UserStatusDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@Transactional
public class UserStatusDAOImpl implements UserStatusDAO {

    @Autowired
    EntityManager entityManager;

    @Override
    public void addStatusForAllUsers(Long status_id) {
        entityManager.createNativeQuery("insert into user_status (user_id, status_id, is_invisible, position)" +
                "select u.user_id, :status_id, true, 0 from user u")
                .setParameter("status_id", status_id)
                .executeUpdate();
    }

    @Override
    public void addUserForAllStatuses(Long user_id) {
        entityManager.createNativeQuery("insert into user_status (user_id, status_id, is_invisible, position)" +
                "select :user_id, s.status_id, false, 0 from status s")
                .setParameter("user_id", user_id)
                .executeUpdate();
    }

    @Override
    public void deleteStatus(Long status_id) {
        entityManager.createNativeQuery("delete from user_status where status_id = :status_id")
                .setParameter("status_id", status_id)
                .executeUpdate();
    }

    @Override
    public void deleteUser(Long user_id) {
        entityManager.createNativeQuery("delete from user_status where user_id = :user_id")
                .setParameter("user_id", user_id)
                .executeUpdate();
    }

    @Override
    public void updateUserStatus(Long user_id, Long status_id, boolean is_invisible, Long position) {
        entityManager.createNativeQuery("update user_status set is_invisible = :is_invisible, position = :position where user_id = :user_id and status_id = :status_id")
                .setParameter("is_invisible", is_invisible)
                .setParameter("position", position)
                .setParameter("user_id", user_id)
                .setParameter("status_id", status_id)
                .executeUpdate();
    }

    @Override
    public UserStatus getUserStatus(Long user_id, Long status_id) {
        UserStatus userStatus = entityManager.createQuery(
                "select us from UserStatus us where us.user_id = :user_id and us.status_id = :status_id", UserStatus.class)
                .setParameter("user_id", user_id)
                .setParameter("status_id", status_id)
                .getSingleResult();
        return userStatus;
    }

    @Override
    public List<UserStatus> getStatusByUserId(Long user_id) {
        List<UserStatus> userStatusList = entityManager.createQuery(
                "select us from UserStatus us where us.user_id = :user_id", UserStatus.class)
                .setParameter("user_id", user_id)
                .getResultList();
        return userStatusList;
    }

    @Override
    public List<UserStatus> getUserByStatusId(Long status_id) {
        List<UserStatus> userStatusList = entityManager.createQuery(
                "select us from UserStatus us where us.status_id = :status_id", UserStatus.class)
                .setParameter("status_id", status_id)
                .getResultList();
        return userStatusList;
    }

    @Override
    public List<UserStatus> getAll() {
        List<UserStatus> userStatusList = entityManager.createQuery(
                "select us from UserStatus us", UserStatus.class)
                .getResultList();
        return userStatusList;
    }
}
