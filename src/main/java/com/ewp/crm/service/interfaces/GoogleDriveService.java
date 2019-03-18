package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.ContractFormData;

import java.util.Optional;

public interface GoogleDriveService {
    Optional<String> createContractWithData(ContractFormData data);
}
