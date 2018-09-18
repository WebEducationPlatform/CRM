package com.ewp.crm.service.interfaces;


import com.ewp.crm.models.Role;
import com.ewp.crm.models.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService extends CommonService<User> {

	User getByEmailOrPhone(String email, String phone);

	void add(User user);

	void update(User user);

	void addPhoto(MultipartFile file, User user);

	List<User> getByRole(Role role);

	User getUserByEmail(String email);

	User getUserByFirstNameAndLastName(String firstName, String lastName);
}
