package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.CallRecord;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface CallRecordRepository extends CommonGenericRepository<CallRecord> {

    @Query("FROM CallRecord callRecord WHERE callRecord.client IS NULL ORDER BY callRecord.id DESC")
    List<CallRecord> getAllCommonRecords(Pageable pageable);

}