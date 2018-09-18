package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.User;

public interface UserDAO extends CommonGenericRepository<User> {

	User getUserByEmailOrPhoneNumber(String email, String phoneNumber);

	User getUserByEmail(String email);

	User getUserByFirstNameAndLastName(String firstName, String lastName);
}
