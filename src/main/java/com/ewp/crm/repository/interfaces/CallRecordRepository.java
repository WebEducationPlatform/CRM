package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.CallRecord;
import org.springframework.data.jpa.repository.Query;

public interface CallRecordRepository extends CommonGenericRepository<CallRecord> {
   // @Query("SELECT cl FROM CallRecord cl WHERE MAX(cl.id)")
    @Query("SELECT cl FROM CallRecord cl WHERE cl.id = (SELECT max(c.id) from CallRecord c)")
    CallRecord findCallRecordMaxId();
}
