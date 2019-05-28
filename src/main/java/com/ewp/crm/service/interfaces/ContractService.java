package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.ContractDataForm;
import com.ewp.crm.models.ContractLinkData;
import com.ewp.crm.models.ContractSetting;

import java.util.Map;

public interface ContractService {

    Map<String,String> getContractIdByFormDataWithSetting(ContractDataForm data, ContractSetting setting);

    boolean updateContractLink(ContractLinkData contractLinkData);

    void deleteContractFromGoogleDrive(String idFileInGoogleDrive);
}