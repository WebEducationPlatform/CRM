package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.ClientHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientHistoryRepository extends JpaRepository<ClientHistory, Long> {
	List<ClientHistory> findByClientId(long id);
}
