package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.ClientHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientHistoryRepository extends JpaRepository<ClientHistory, Long>, ClientHistoryRepositoryCustom {

	List<ClientHistory> getByClientId(long id);

	List<ClientHistory> getAllByClientId(long id, Pageable pageable);

	ClientHistory getFirstByClientId(long id);
}
