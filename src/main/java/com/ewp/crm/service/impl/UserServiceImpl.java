package com.ewp.crm.service.impl;

import com.ewp.crm.models.User;
import com.ewp.crm.repository.interfaces.UserDAO;
import com.ewp.crm.service.interfaces.UserService;
import com.sun.deploy.ref.Helpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
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

	@Override
	public void addPhoto(MultipartFile file, User user) throws IOException, SQLException {

		if (!file.isEmpty()) {
			BufferedImage image;
			image = ImageIO.read(new BufferedInputStream(file.getInputStream()));
			String fileName = file.getOriginalFilename();
			String type = "jpg";
			if (fileName.lastIndexOf(".") != -1) {
				type = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
			}

			Blob blobImg = new SerialBlob(convertToByteArray(image, type));
			user.setPhotoType(type);
			user.setPhoto(blobImg);
			update(user);
		}
	}

	private static byte[] convertToByteArray(BufferedImage bufferedImage, String type) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, type, outputStream);
		outputStream.flush();
		byte[] bytes = outputStream.toByteArray();
		outputStream.close();
		return bytes;
	}
}
