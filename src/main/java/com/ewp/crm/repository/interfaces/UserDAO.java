package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Role;
import com.ewp.crm.models.User;
import com.ewp.crm.models.UserRoutes;
import com.ewp.crm.models.dto.MentorDtoForMentorsPage;
import com.ewp.crm.models.dto.UserRoutesDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.SqlResultSetMapping;
import java.util.List;

public interface UserDAO extends CommonGenericRepository<User>, UserDAOCustom {

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

	User getUserById(Long id);
}
