package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.LastContractId;

public interface LastContractIdRepository extends CommonGenericRepository<LastContractId> {

    LastContractId getById(Long id);
}
