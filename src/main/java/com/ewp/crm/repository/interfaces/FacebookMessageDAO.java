package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.FacebookMessage;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface FacebookMessageDAO extends CommonGenericRepository<FacebookMessage> {

	@Query("SELECT MAX (createdTime) FROM FacebookMessage")
	Date findMaxDate();
}
