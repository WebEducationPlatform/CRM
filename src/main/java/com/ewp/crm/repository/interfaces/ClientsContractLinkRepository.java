package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.ContractLinkData;

import java.util.Optional;

public interface ClientsContractLinkRepository extends CommonGenericRepository<ContractLinkData> {
    Optional<ContractLinkData> getByClientId(Long id);
}