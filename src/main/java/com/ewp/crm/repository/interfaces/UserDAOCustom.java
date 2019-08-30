package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.dto.UserDtoForBoard;

import java.util.List;

public interface UserDAOCustom {

	List<UserDtoForBoard> getAllMentorsForDto();

	List<UserDtoForBoard> getAllWithoutMentorsForDto();
}
