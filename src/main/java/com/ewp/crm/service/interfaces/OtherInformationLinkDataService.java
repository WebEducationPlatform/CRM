package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.OtherInformationLinkData;

import java.util.Optional;

public interface OtherInformationLinkDataService {
    boolean existsByHash(String hash);

    Optional<OtherInformationLinkData> getByHash(String hash);

    void deleteByHash(String hash);

    void save(OtherInformationLinkData otherInformationLinkData);
}