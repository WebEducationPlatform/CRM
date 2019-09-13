package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.CallRecord;
import com.ewp.crm.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface CallRecordRepository extends CommonGenericRepository<CallRecord> {

    @Query("FROM CallRecord callRecord ORDER BY callRecord.id DESC")
    List<CallRecord> getAllCommonRecords(Pageable pageable);

    List<CallRecord> findAllByCallingUserAndDateBetweenOrderByDateDesc(User user, ZonedDateTime from, ZonedDateTime to, Pageable pageable);

    List<CallRecord> findAllByDateBetweenOrderByDateDesc(ZonedDateTime from, ZonedDateTime to, Pageable pageable);

    CallRecord getByClientHistoryId(Long id);
}