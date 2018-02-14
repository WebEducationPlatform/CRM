package com.ewp.crm.service;

import com.ewp.crm.dao.UserDAO;
import com.ewp.crm.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {


    @Autowired
    UserDAO userDAO;


    @Override
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    @Override
    public User getUserByEmail(String email) {
        return userDAO.findUserByEmail(email);
    }

    @Override
    public User getUserByID(Long id) {
        return userDAO.findOne(id);
    }

    @Override
    public void addUser(User user) {
        userDAO.saveAndFlush(user);
    }

    @Override
    public void updateUser(User user) {
        userDAO.saveAndFlush(user);
    }

    @Override
    public void deleteUser(User user) {
        userDAO.delete(user);
    }
}
