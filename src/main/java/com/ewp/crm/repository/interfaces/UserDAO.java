package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Role;
import com.ewp.crm.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserDAO extends CommonGenericRepository<User> {

	User getUserByEmailOrPhoneNumber(String email, String phoneNumber);

	User getUserByEmail(String email);

	List<User> getUserByRole(Role role);

	User getUserByFirstNameAndLastName(String firstName, String lastName);

	@Query("SELECT user FROM User user WHERE user.id = :userId")
	List<User> getUserByVkToken(@Param("userId") long id);

	User getById(Long Id);
}