package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.FacebookMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface FacebookMessageDAO extends CommonGenericRepository<FacebookMessage> {

	@Query("SELECT MAX (createdTime) FROM FacebookMessage")
	Date findMaxDate();
}
