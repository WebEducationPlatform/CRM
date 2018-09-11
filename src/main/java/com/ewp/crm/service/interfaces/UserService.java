package com.ewp.crm.service.interfaces;


import com.ewp.crm.models.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService extends CommonService<User> {

	User getByEmailOrPhone(String email, String phone);

	User add(User user);

	void update(User user);

	void addPhoto(MultipartFile file, User user);

	User getUserByEmail(String email);

	User getUserByFirstNameAndLastName(String firstName, String lastName);
}
