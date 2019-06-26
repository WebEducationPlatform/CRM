package com.ewp.crm.service.impl;

import com.ewp.crm.models.OtherInformationLinkData;
import com.ewp.crm.repository.interfaces.OtherInformationLinkDataRepository;
import com.ewp.crm.service.interfaces.OtherInformationLinkDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OtherInformationLinkDataServiceImpl implements OtherInformationLinkDataService {

    private OtherInformationLinkDataRepository otherInformationLinkDataRepository;

    @Autowired
    public OtherInformationLinkDataServiceImpl(OtherInformationLinkDataRepository otherInformationLinkDataRepository) {
        this.otherInformationLinkDataRepository = otherInformationLinkDataRepository;
    }

    @Override
    public boolean existsByHash(String hash) {
        return otherInformationLinkDataRepository.existsByHash(hash);
    }

    @Override
    public Optional<OtherInformationLinkData> getByHash(String hash) {
        return Optional.of(otherInformationLinkDataRepository.getByHash(hash));
    }

    @Override
    public void deleteByHash(String hash) {
        otherInformationLinkDataRepository.deleteByHash(hash);
    }

    @Override
    public void save(OtherInformationLinkData otherInformationLinkData) {
        otherInformationLinkDataRepository.save(otherInformationLinkData);
    }
}