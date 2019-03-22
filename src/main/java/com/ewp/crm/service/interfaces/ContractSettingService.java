package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.ContractSetting;

import java.util.Optional;

public interface ContractSettingService {

    boolean existsByHash(String hash);

    Optional<ContractSetting> getByHash(String hash);

    void deleteByHash(String hash);
}
