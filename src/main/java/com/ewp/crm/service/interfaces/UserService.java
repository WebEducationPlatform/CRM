package com.ewp.crm.service.interfaces;


import com.ewp.crm.models.Role;
import com.ewp.crm.models.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService extends CommonService<User> {

	Optional<User> getByEmailOrPhone(String email, String phone);

	User add(User user);

	void update(User user);

	void removeUserWithAllServices(Long id);

	void addPhoto(MultipartFile file, User user);

	List<User> getByRole(Role role);

	Optional<User> getUserByEmail(String email);

	Optional<User> getUserByFirstNameAndLastName(String firstName, String lastName);

	void setColorBackground(String color, User user);

	List<User> getUserByVkToken(long id);
}
