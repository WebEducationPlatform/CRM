package com.ewp.crm.service.impl;

import com.ewp.crm.configs.ImageConfig;
import com.ewp.crm.exceptions.user.UserExistsException;
import com.ewp.crm.exceptions.user.UserPhotoException;
import com.ewp.crm.models.Mentor;
import com.ewp.crm.models.Role;
import com.ewp.crm.models.Status;
import com.ewp.crm.models.User;
import com.ewp.crm.models.UserRoutes;
import com.ewp.crm.models.dto.MentorDtoForMentorsPage;
import com.ewp.crm.models.dto.UserDtoForBoard;
import com.ewp.crm.models.dto.UserRoutesDto;
import com.ewp.crm.repository.interfaces.MentorDao;
import com.ewp.crm.repository.interfaces.UserDAO;
import com.ewp.crm.service.interfaces.RoleService;
import com.ewp.crm.service.interfaces.UserRoutesService;
import com.ewp.crm.service.interfaces.UserService;
import com.ewp.crm.service.interfaces.UserStatusService;
import com.ewp.crm.util.validators.PhoneValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl extends CommonServiceImpl<User> implements UserService {
    private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserDAO userDAO;
    private final ImageConfig imageConfig;
    private final PhoneValidator phoneValidator;
    private Environment env;
    private final EntityManager entityManager;
    private final RoleService roleService;
    private final UserRoutesService userRoutesService;
    private final UserStatusService userStatusService;
    private final MentorDao mentorDao;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserDAO userDAO, ImageConfig imageConfig, PhoneValidator phoneValidator,
                           Environment env, EntityManager entityManager, RoleService roleService,
                           UserRoutesService userRoutesService, UserStatusService userStatusService, MentorDao mentorDao) {
        this.userDAO = userDAO;
        this.imageConfig = imageConfig;
        this.phoneValidator = phoneValidator;
        this.env = env;
        this.entityManager = entityManager;
        this.roleService = roleService;
        this.userRoutesService = userRoutesService;
        this.userStatusService = userStatusService;
        this.mentorDao = mentorDao;
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
            throw new UserExistsException(env.getProperty("messaging.user.exception.allready-exist"));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        logger.info("{}: user saved successfully", UserServiceImpl.class.getName());
        User addUser = userDAO.saveAndFlush(user);
        if (addUser != null) {
            userStatusService.addUserForAllStatuses(addUser.getId());
        }
        return addUser;
    }

    @Override
    public void delete(Long user_id) {
        logger.info("{} deleting of the user...", UserServiceImpl.class.getName());
        Optional<User> optional = userDAO.findById(user_id);
        User user = null;
        if (optional.isPresent()) {
            user = optional.get();
        }
        userDAO.delete(user);
        userStatusService.deleteUser(user_id);
        logger.info("{} user deleted successfully...", UserServiceImpl.class.getName());
    }

    @Override
    public void delete(User user) {
        logger.info("{} deleting of the user...", UserServiceImpl.class.getName());
        delete(user.getId());
        logger.info("{} user deleted successfully...", UserServiceImpl.class.getName());
    }

    @Override
    public void update(User user) {
        String username = "unknown";
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            username = authentication.getName();
        } catch (Exception ignore) {

        }
        logger.info("{}: user {} executed updating of a user id={}, name={}, roles={}, stacktrace:\n{}",
                UserServiceImpl.class.getName(),
                username,
                user.getId(), user.getFullName(),
                user.getRole().toString(),
                Arrays.toString(new Throwable().getStackTrace()));
        user.setPhoneNumber(phoneValidator.phoneRestore(user.getPhoneNumber()));
        User userFromDb = userDAO.getOne(user.getId());
        if (userFromDb != null) {
            userFromDb.setFirstName(user.getFirstName());
            userFromDb.setLastName(user.getLastName());
            userFromDb.setEmail(user.getEmail());
            userFromDb.setBirthDate(user.getBirthDate());
            userFromDb.setPhoneNumber(user.getPhoneNumber());
            userFromDb.setVk(user.getVk());
            userFromDb.setSex(user.getSex());
            userFromDb.setCountry(user.getCountry());
            userFromDb.setCity(user.getCity());
            userFromDb.setIpTelephony(user.isIpTelephony());
            userFromDb.setRole(user.getRole());
            userFromDb.setEnableMailNotifications(user.isEnableMailNotifications());
            userFromDb.setEnableSmsNotifications(user.isEnableSmsNotifications());
            userFromDb.setEnableAsignMentorMailNotifications(user.isEnableAsignMentorMailNotifications());
            userFromDb.setIsVerified(user.isVerified());
            userFromDb.setEnabled(user.isEnabled());
            userFromDb.setRowStatusDirection(user.getRowStatusDirection());
            if (user.getPassword().length() > 0) {
                if (!user.getPassword().equals(userFromDb.getPassword())){
                    userFromDb.setPassword(passwordEncoder.encode(user.getPassword()));
                }
            }
            logger.info("{}: user updated successfully", UserServiceImpl.class.getName());
            User.UserType userType = userDAO.getUserType(user.getId());
            if (userType.equals(User.UserType.MENTOR)) {
                mentorDao.saveAndFlush(new Mentor(userFromDb));
            }else {
                userDAO.saveAndFlush(userFromDb);
            }
        }
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
                user.setPhoto("/rest/admin/user/avatar/" + fileName);
                update(user);
            } catch (Exception e) {
                logger.error("Error during saving photo: " + e.getMessage());
                throw new UserPhotoException(env.getProperty("messaging.user.exception.photo-save-error"));
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

    @Override
    public Optional<User> getUserToOwnCard() {
        return getUserToOwnCard(null) ;
    }

    @Override
    public Optional<User> getUserToOwnCard(UserRoutes.UserRouteType routetype) {
        User userToOwnClient = null;
        if (routetype == null) {
            long roleId = roleService.getRoleByName("HR").getId();
            userToOwnClient = userDAO.getUserByRoleIdAndLastClientDate(roleId);
            userToOwnClient.setLastClientDate(Instant.now());
            update(userToOwnClient);

        } else {
            if (routetype == UserRoutes.UserRouteType.FROM_JM_EMAIL){
                List<UserRoutesDto> userRoutes = userRoutesService.getUserByRoleAndUserRoutesType("HR", UserRoutes.UserRouteType.FROM_JM_EMAIL.name());
                Long userId = userRoutesService.getUserIdByPercentChance(userRoutes);
                userToOwnClient = userDAO.getUserById(userId);
                userToOwnClient.setLastClientDate(Instant.now());
                update(userToOwnClient);
            }
        }
        return Optional.ofNullable(userToOwnClient);
    }


    @Override
    public List<MentorDtoForMentorsPage> getAllMentors() {
        return userDAO.getAllMentors();
    }

    public Optional<List<UserDtoForBoard>> getAllMentorsForDto() {
        return Optional.ofNullable(userDAO.getAllMentorsForDto());
    }

    @Override
    public Optional<List<UserDtoForBoard>> getAllWithoutMentorsForDto() {
        return Optional.ofNullable(userDAO.getAllWithoutMentorsForDto());
    }
}
