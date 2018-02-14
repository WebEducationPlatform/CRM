package com.ewp.crm.service;

import com.ewp.crm.models.User;
import org.springframework.stereotype.Service;

import java.util.List;


public interface UserService {
    List<User> getAllUsers();

    User getUserByEmail(String name);

    User getUserByID(Long id);

    void addUser(User user);

    void updateUser(User user);

    void deleteUser(User user);

}
