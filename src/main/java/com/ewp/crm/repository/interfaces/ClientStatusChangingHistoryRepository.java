package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.ClientStatusChangingHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface ClientStatusChangingHistoryRepository extends CommonGenericRepository<ClientStatusChangingHistory>, ClientStatusChangingHistoryRepositoryCustom {

    List<ClientStatusChangingHistory> findAllByClientId(Long clientId);

    List<ClientStatusChangingHistory> findAllByClientIdOrderByDateAsc(Long clientId);

    @Query(value = "SELECT * FROM `client_status_changing_history` `c` WHERE `c`.`client_id` = :id ORDER BY `c`.`date` ASC LIMIT 0,1", nativeQuery = true)
    ClientStatusChangingHistory getFirstByClientId(@Param("id") Long clientId);

    @Query(value = "SELECT * FROM `client_status_changing_history` `c` WHERE `c`.`client_id` = :id ORDER BY `c`.`date` DESC LIMIT 0,1", nativeQuery = true)
    ClientStatusChangingHistory getTopByClientId(@Param("id") Long clientId);

}
