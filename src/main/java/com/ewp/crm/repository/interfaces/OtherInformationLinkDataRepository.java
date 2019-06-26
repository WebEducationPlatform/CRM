package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.OtherInformationLinkData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface OtherInformationLinkDataRepository extends CommonGenericRepository<OtherInformationLinkData> {

    boolean existsByHash(String hash);

    void deleteByHash(String hash);

    OtherInformationLinkData getByHash(String hash);
}