package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDAO extends JpaRepository<User, Long> {
	User getUserByEmailOrPhoneNumber(String email, String phoneNumber);
	User getUserByEmail(String email);
}
