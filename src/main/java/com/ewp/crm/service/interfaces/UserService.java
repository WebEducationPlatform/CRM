package com.ewp.crm.service.interfaces;


import com.ewp.crm.models.User;

import java.util.List;

public interface UserService {

	List<User> getAll();

	User get(Long id);

	User getByEmailOrPhone(String email, String phone);

	void add(User user);

	void update(User user);

	void delete(Long id);

	void delete(User user);
}
