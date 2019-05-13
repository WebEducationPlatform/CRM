package com.ewp.crm.service.impl;

import com.ewp.crm.configs.ImageConfig;
import com.ewp.crm.exceptions.user.UserExistsException;
import com.ewp.crm.exceptions.user.UserPhotoException;
import com.ewp.crm.models.Role;
import com.ewp.crm.models.User;
import com.ewp.crm.repository.interfaces.UserDAO;
import com.ewp.crm.service.interfaces.UserService;
import com.ewp.crm.util.validators.PhoneValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl extends CommonServiceImpl<User> implements UserService {
    private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserDAO userDAO;
    private final ImageConfig imageConfig;
    private final PhoneValidator phoneValidator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserDAO userDAO, ImageConfig imageConfig,PhoneValidator phoneValidator) {
        this.userDAO = userDAO;
        this.imageConfig = imageConfig;
        this.phoneValidator = phoneValidator;
    }

    @Override
    public Optional<User> getByEmailOrPhone(String email, String phone) {
        return Optional.ofNullable(userDAO.getUserByEmailOrPhoneNumber(email, phone));
    }

    @Override
    public List<User> getByRole(Role role) {
        return userDAO.getUserByRole(role);
    }

    @Override
    public User add(User user) {
        logger.info("{}: adding of a new user...", UserServiceImpl.class.getName());
        user.setPhoneNumber(phoneValidator.phoneRestore(user.getPhoneNumber()));
        if (userDAO.getUserByEmail(user.getEmail()) != null) {
            logger.warn("{}: user with email {} is already exist", UserServiceImpl.class.getName(), user.getEmail());
            throw new UserExistsException();
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        logger.info("{}: user saved successfully", UserServiceImpl.class.getName());
        return userDAO.saveAndFlush(user);
    }

    @Override
    public void update(User user) {
        logger.info("{}: updating of a user...", UserServiceImpl.class.getName());
        user.setPhoneNumber(phoneValidator.phoneRestore(user.getPhoneNumber()));
        User currentUserByEmail;
        if ((currentUserByEmail = userDAO.getUserByEmail(user.getEmail())) != null && !currentUserByEmail.getId().equals(user.getId())) {
            logger.warn("{}: user with email {} is already exist", UserServiceImpl.class.getName(), user.getEmail());
            throw new UserExistsException();
        }

        if (!user.getPassword().equals(userDAO.getOne(user.getId()).getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        logger.info("{}: user updated successfully", UserServiceImpl.class.getName());
        userDAO.saveAndFlush(user);
    }

    @Override
    public void addPhoto(MultipartFile file, User user) {
        logger.info("{}: adding of a photo...", UserServiceImpl.class.getName());
        if (!file.isEmpty()) {
            try {
                BufferedImage image = ImageIO.read(new BufferedInputStream(file.getInputStream()));
                String fileName = "user-" + user.getId() + "-avatar.png";
                File outputFile = new File(imageConfig.getPathForAvatar() + fileName);
                ImageIO.write(image, "png", outputFile);
                user.setPhoto("/admin/avatar/" + fileName);
                update(user);
            } catch (Exception e) {
                logger.error("Error during saving photo: " + e.getMessage());
                throw new UserPhotoException();
            }
            logger.info("{}: photo added successfully", UserServiceImpl.class.getName());
        }
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return Optional.ofNullable(userDAO.getUserByEmail(email));
    }

    @Override
    public Optional<User> getUserByFirstNameAndLastName(String firstName, String lastName) {
        return Optional.ofNullable(userDAO.getUserByFirstNameAndLastName(firstName, lastName));
    }

    @Override
    public void setColorBackground(String color, User user) {
        String precolor = user.getColorBackground();
        user.setColorBackground(color);
        update(user);

        logger.info("{}: color background set from {} to {}", user.getFullName(), precolor, color);
    }

    @Override
    public List<User> getUserByVkToken(long id) {
        return userDAO.getUserByVkToken(id);
    }
}
