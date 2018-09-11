package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.AssignSkypeCall;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AssignSkypeCallRepository extends CommonGenericRepository<AssignSkypeCall> {

	@Query(value = "select sl from AssignSkypeCall sl where now() >= sl.remindBeforeOfSkypeCall")
	List<AssignSkypeCall> getSkypeCallDate();
}