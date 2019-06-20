package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.OtherInformationLinkData;

public interface OtherInformationLinkDataRepository extends CommonGenericRepository<OtherInformationLinkData> {

    boolean existsByHash(String hash);

    void deleteByHash(String hash);

    OtherInformationLinkData getByHash(String hash);
}