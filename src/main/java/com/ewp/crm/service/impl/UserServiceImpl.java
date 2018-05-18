package com.ewp.crm.service.impl;

import com.ewp.crm.configs.ImageConfig;
import com.ewp.crm.exceptions.user.UserExistsException;
import com.ewp.crm.exceptions.user.UserPhotoException;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.User;
import com.ewp.crm.repository.interfaces.UserDAO;
import com.ewp.crm.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

	private final UserDAO userDAO;
	private final ImageConfig imageConfig;

	private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

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
		phoneNumberValidation(user);
		if (userDAO.getUserByEmail(user.getEmail()) != null) {
			throw new UserExistsException();
		}
		userDAO.saveAndFlush(user);
	}

	@Override
	public void update(User user) {
		phoneNumberValidation(user);
		User currentUserByEmail;
		if ((currentUserByEmail = userDAO.getUserByEmail(user.getEmail())) != null && !currentUserByEmail.getId().equals(user.getId())) {
			throw new UserExistsException();
		}
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
	public void addPhoto(MultipartFile file, User user) {
		if (!file.isEmpty()) {
			try {
				BufferedImage image = ImageIO.read(new BufferedInputStream(file.getInputStream()));
				String fileName = "user-" + user.getId() + "-avatar.png";
				File outputFile = new File(imageConfig.getPathForAvatar() + fileName);
				ImageIO.write(image, "png", outputFile);
				user.setPhoto("/admin/avatar/" + fileName);
				update(user);
			}catch (Exception e){
				logger.error("Error during saving photo: " + e.getMessage());
				throw new UserPhotoException();
			}
		}
	}

	@Override
	public User getUserByEmail(String email) {
		return userDAO.getUserByEmail(email);
	}

	@Override
	public User getUserByFirstNameAndLastName(String firstName, String lastName) {
		return userDAO.getUserByFirstNameAndLastName(firstName, lastName);
	}

	private void phoneNumberValidation(User user) {
		String phoneNumber = user.getPhoneNumber();
		Pattern pattern = Pattern.compile("^((8|\\+7|7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$");
		Matcher matcher = pattern.matcher(phoneNumber);
		if (matcher.matches()) {
			if (phoneNumber.startsWith("8")) {
				phoneNumber = phoneNumber.replaceFirst("8", "7");
			}
			user.setPhoneNumber(phoneNumber.replaceAll("[+()-]", "")
					.replaceAll("\\s", ""));
		}
	}
}
