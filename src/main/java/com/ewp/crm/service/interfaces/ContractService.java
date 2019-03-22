package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.ContractDataForm;
import com.ewp.crm.models.ContractSetting;

import java.util.Optional;

public interface GoogleDriveService {

    Optional<String> getContractIdByFormDataWithSetting(ContractDataForm data, ContractSetting setting);
}
