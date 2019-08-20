package com.ewp.crm.service.impl;

import com.ewp.crm.configs.ImageConfig;
import com.ewp.crm.controllers.rest.IPTelephonyRestController;
import com.ewp.crm.exceptions.user.UserExistsException;
import com.ewp.crm.exceptions.user.UserPhotoException;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.Role;
import com.ewp.crm.models.User;
import com.ewp.crm.models.UserRoutes;
import com.ewp.crm.models.dto.ClientHistoryDto;
import com.ewp.crm.models.dto.MentorDtoForMentorsPage;
import com.ewp.crm.models.dto.UserRoutesDto;
import com.ewp.crm.repository.interfaces.UserDAO;
import com.ewp.crm.service.interfaces.RoleService;
import com.ewp.crm.service.interfaces.UserService;
import com.ewp.crm.util.validators.PhoneValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class UserServiceImpl extends CommonServiceImpl<User> implements UserService {
    private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserDAO userDAO;
    private final ImageConfig imageConfig;
    private final PhoneValidator phoneValidator;
    private Environment env;
    private final EntityManager entityManager;
    private final RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserDAO userDAO, ImageConfig imageConfig, PhoneValidator phoneValidator,
                           Environment env,  EntityManager entityManager, RoleService roleService) {
        this.userDAO = userDAO;
        this.imageConfig = imageConfig;
        this.phoneValidator = phoneValidator;
        this.env = env;
        this.entityManager = entityManager;
        this.roleService = roleService;
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
        return userDAO.saveAndFlush(user);
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
        User currentUserByEmail;
        if ((currentUserByEmail = userDAO.getUserByEmail(user.getEmail())) != null && !currentUserByEmail.getId().equals(user.getId())) {
            logger.warn("{}: user with email {} is already exist", UserServiceImpl.class.getName(), user.getEmail());
            throw new UserExistsException(env.getProperty("messaging.user.exception.allready-exist"));
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
    @Transactional
    public Optional<User> getUserToOwnCard(UserRoutes.UserRouteType routetype) {
        User userToOwnClient = null;
        if (routetype == null) {
            long roleId = roleService.getRoleByName("HR").getId();
            try {
                String query =
                        "SELECT u.*, p.*, 1 AS clazz_, FALSE AS mentor_show_only_my_clients " +
                                "   FROM  user u " +
                                "       LEFT JOIN permissions p ON u.user_id = p.user_id " +
                                "   WHERE " +
                                "       u.user_id = p.user_id AND " +
                                "       p.role_id = :roleId " +
                                "   ORDER BY u.last_client_date " +
                                "   LIMIT 1;";
                userToOwnClient = (User) entityManager.createNativeQuery(query, User.class)
                        .setParameter("roleId", roleId)
                        .getSingleResult();
                logger.info("Coordinator for new client card to own found: " + userToOwnClient.getFullName());
                userToOwnClient.setLastClientDate(Instant.now());
                update(userToOwnClient);
            } catch (Exception e) {
                logger.error("Can't find coordinator for new client card to own roleId = {}", roleId, e);
            }

        } else {
            if (routetype == UserRoutes.UserRouteType.FROM_JM_EMAIL){
                List<UserRoutesDto> userRoutes = getUserByRoleAndUserRoutesType("HR", UserRoutes.UserRouteType.FROM_JM_EMAIL.name());
                Long userId = getUserIdByPercentChance(userRoutes);
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

    @Override
    public void updateClientRoutes(List<UserRoutesDto> userRoutesDtoListist) {
      for (UserRoutesDto routesDto: userRoutesDtoListist) {
                User hrUser =  get(routesDto.getUser_id());
                Set<UserRoutes> userRoutes = hrUser.getUserRoutes() != null ? hrUser.getUserRoutes(): new HashSet<>();
                UserRoutes uRoutes = UserRoutesDto.getUserRoutesFromDto(routesDto);
                uRoutes.setUser(hrUser);
                if (userRoutes.contains(uRoutes)){
                    for (UserRoutes rout: userRoutes ) {
                        if (rout.equals(uRoutes)){
                            rout.setWeight(uRoutes.getWeight());
                        }
                    }
                } else {
                    userRoutes.add(uRoutes);
                }
                hrUser.setUserRoutes(userRoutes);
                update(hrUser);
            }
    }

    private Long getUserIdByPercentChance(List<UserRoutesDto> userRoutesList){
        Long[] userIds = new Long[100];
        int currentId = 0;
        int currSumm = 0;
        Collections.sort(userRoutesList,new Comparator<UserRoutesDto>() {
            public int compare(UserRoutesDto o1, UserRoutesDto o2) {
                return (o1.getWeight() < o2.getWeight()) ? -1 : 1;
            }
        });
        for (int i = 0; i < userIds.length; i++) {
            if (userRoutesList.get(currentId).getWeight() > (currSumm + i)) {
                userIds[i] = userRoutesList.get(currentId).getUser_id();
            }
            else{
                currSumm += userRoutesList.get(currentId).getWeight();
                if (currentId < userRoutesList.size()-1 ) {
                    currentId++;
                }
                userIds[i] = userRoutesList.get(currentId).getUser_id();
            }
        }
        return userIds[(int) (Math.random() * 99)];
    }

    private List<UserRoutesDto>  getUserByRoleAndUserRoutesType(String userRole, String userRouteType){
        List<UserRoutesDto> result = new ArrayList<>();

        String sqlQuery =
                " SELECT " +
					" ur.user_routes_id as id, u.user_id as user_id, ur.weight as weight, ur.user_route_type as userRouteType" +
					" FROM  user_routes ur" +
					" LEFT JOIN user u  on ur.user_id = u.user_id" +
					" LEFT JOIN permissions p on p.user_id= u.user_id" +
					" JOIN role r on  r.id = p.role_id" +
					" WHERE r.role_name = :role" +
					" AND ur.user_route_type = :routetype";

        List<Tuple> tuples = entityManager.createNativeQuery(sqlQuery, Tuple.class)
                .setParameter("role", userRole)
                .setParameter("routetype", userRouteType)
                .getResultList();

        for (Tuple tuple :tuples) {
            result.add(new UserRoutesDto(
                    ((BigInteger) tuple.get("user_id")).longValue(),
                    (Integer) tuple.get("weight"),
                    (String) tuple.get("userRouteType")
            ));
        }
        return result;
    }
}
