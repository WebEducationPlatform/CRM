package com.ewp.crm.service.interfaces;


import com.ewp.crm.models.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface UserService {

	List<User> getAll();

	User get(Long id);

	User getByEmailOrPhone(String email, String phone);

	void add(User user);

	void update(User user);

	void delete(Long id);

	void delete(User user);

	void addPhoto(MultipartFile file, User user) throws IOException, SQLException;
}
