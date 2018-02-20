package com.ewp.crm.service.impl;

import com.ewp.crm.models.User;
import com.ewp.crm.repository.interfaces.UserDAO;
import com.ewp.crm.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

	private final UserDAO userDAO;

	@Autowired
	public UserServiceImpl(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Override
	public List<User> getAll() {
		return userDAO.findAll();
	}

	@Override
	public User get(Long id) {
		return userDAO.findOne(id);
	}

	@Override
	public User getByEmailOrPhone(String email, String phone) {
		return userDAO.getUserByEmailOrPhoneNumber(email, phone);
	}

	@Override
	public void add(User user) {
		userDAO.saveAndFlush(user);
	}

	@Override
	public void update(User user) {
		userDAO.saveAndFlush(user);
	}

	@Override
	public void delete(Long id) {
		userDAO.delete(id);
	}

	@Override
	public void delete(User user) {
		userDAO.delete(user);
	}
}
