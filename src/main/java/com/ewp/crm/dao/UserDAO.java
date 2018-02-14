package com.ewp.crm.dao;

import com.ewp.crm.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserDAO extends JpaRepository<User, Long> {
    User findUserByEmail(String Email);
}
