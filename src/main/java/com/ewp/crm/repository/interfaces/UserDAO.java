package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Role;
import com.ewp.crm.models.User;

import java.util.List;

public interface UserDAO extends CommonGenericRepository<User> {

	User getUserByEmailOrPhoneNumber(String email, String phoneNumber);

	User getUserByEmail(String email);

	List<User> getUserByRole(Role role);

	User getUserByFirstNameAndLastName(String firstName, String lastName);
}
