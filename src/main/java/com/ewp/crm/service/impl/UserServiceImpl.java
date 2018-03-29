package com.ewp.crm.service.impl;

import com.ewp.crm.configs.ImageConfig;
import com.ewp.crm.models.User;
import com.ewp.crm.repository.interfaces.UserDAO;
import com.ewp.crm.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

	private final UserDAO userDAO;
	private final ImageConfig imageConfig;

	@Autowired
	public UserServiceImpl(UserDAO userDAO, ImageConfig imageConfig) {
		this.userDAO = userDAO;
		this.imageConfig = imageConfig;
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

	@Override
	public void addPhoto(MultipartFile file, User user) throws IOException, SQLException {

		if (!file.isEmpty()) {
			BufferedImage image = ImageIO.read(new BufferedInputStream(file.getInputStream()));
			String fileName = "user-" + user.getId() + "-avatar.png";
			File outputFile = new File(imageConfig.getPathForAvatar() + fileName);
			ImageIO.write(image, "png", outputFile);
			user.setPhoto("/avatar/" + fileName);
			update(user);
		}
	}
}
