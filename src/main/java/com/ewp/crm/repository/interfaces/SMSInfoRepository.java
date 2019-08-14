package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.SMSInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SMSInfoRepository extends CommonGenericRepository<SMSInfo> {

	List<SMSInfo> getByIsChecked(Boolean isDelivered);

	List<SMSInfo> findAll();

	@Modifying
	@Transactional
//	@Query("DELETE FROM SMSInfo s WHERE s.id IN (SELECT s.id FROM SMSInfo s JOIN s.user su WHERE su.id = :userId)")
	@Query("DELETE FROM SMSInfo s WHERE s.user.id = :userId")
	void deleteAllByUserId(@Param("userId") long id);

}
