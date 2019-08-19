package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Role;
import com.ewp.crm.models.User;
import com.ewp.crm.models.UserRoutes;
import com.ewp.crm.models.dto.MentorDtoForMentorsPage;
import com.ewp.crm.models.dto.UserRoutesDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserDAO extends CommonGenericRepository<User> {

	User getUserByEmailOrPhoneNumber(String email, String phoneNumber);

	User getUserByEmail(String email);

	List<User> getUserByRole(Role role);

	User getUserByFirstNameAndLastName(String firstName, String lastName);

	@Query("SELECT user FROM User user WHERE user.id = :userId")
	List<User> getUserByVkToken(@Param("userId") long id);

	@Query(nativeQuery = true,
	value = "SELECT u.user_id, u.email FROM user u " +
			"LEFT JOIN permissions p ON p.user_id = u.user_id " +
			"LEFT JOIN role r ON p.role_id = r.id " +
			"WHERE r.role_name = 'MENTOR'")
    List<MentorDtoForMentorsPage> getAllMentors();

	@Query(nativeQuery = true,
			value = " SELECT " +
					" u.user_id, ur.user_route_type, ur.weight" +
					" FROM  user_routes ur" +
					" LEFT JOIN user u  on ur.user_id = u.user_id" +
					" LEFT JOIN permissions p on p.user_id= u.user_id" +
					" JOIN role r on  r.id = p.role_id" +
					" WHERE r.role_name = 1?" +
					" AND ur.user_route_type = 2?")
	List<UserRoutesDto> getUserByRoleAndUserRoutesType(String role, UserRoutes.UserRouteType routeType);
}
