package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.CallRecord;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface CallRecordRepository extends CommonGenericRepository<CallRecord> {

    @Query("from CallRecord callRecord where callRecord.client is null order by callRecord.id desc")
    List<CallRecord> getAllCommonRecords(Pageable pageable);

}