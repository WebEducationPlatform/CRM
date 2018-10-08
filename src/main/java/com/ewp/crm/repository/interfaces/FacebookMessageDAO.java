package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.FacebookMessage;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface FacebookMessageDAO extends CommonGenericRepository<FacebookMessage> {

	@Query("SELECT MAX (createdTime) FROM FacebookMessage")
	LocalDateTime findMaxDate();
}
