package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.ContractSetting;

public interface ContractSettingRepository extends CommonGenericRepository<ContractSetting> {

    boolean existsByHash(String hash);

    void deleteByHash(String hash);

    ContractSetting getByHash(String hash);
}
