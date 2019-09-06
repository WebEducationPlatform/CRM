package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.User;
import com.ewp.crm.models.dto.UserDtoForBoard;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserDAOCustom {

	List<UserDtoForBoard> getAllMentorsForDto();

	List<UserDtoForBoard> getAllWithoutMentorsForDto();

	@Transactional
	User getUserByRoleIdAndLastClientDate(long roleId);

	void addUserAllStatuss(Long user_id);

	void deleteUserInSortStatuses(Long user_id);
}
