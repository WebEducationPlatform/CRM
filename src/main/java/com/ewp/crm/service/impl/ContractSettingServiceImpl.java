package com.ewp.crm.service.impl;

import com.ewp.crm.models.ContractSetting;
import com.ewp.crm.repository.interfaces.ContractSettingRepository;
import com.ewp.crm.service.interfaces.ContractSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ContractSettingServiceImpl implements ContractSettingService {

    private final ContractSettingRepository contractSettingRepository;

    @Autowired
    public ContractSettingServiceImpl(ContractSettingRepository contractSettingRepository) {
        this.contractSettingRepository = contractSettingRepository;
    }

    @Override
    public boolean existsByHash(String hash) {
        return contractSettingRepository.existsByHash(hash);
    }

    @Override
    public Optional<ContractSetting> getByHash(String hash) {
        return Optional.of(contractSettingRepository.getByHash(hash));
    }

    @Override
    public void deleteByHash(String hash) {
        contractSettingRepository.deleteByHash(hash);
    }
}
