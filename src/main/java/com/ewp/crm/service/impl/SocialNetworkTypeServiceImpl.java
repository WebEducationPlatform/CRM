package com.ewp.crm.service.impl;

import com.ewp.crm.models.SocialNetworkType;
import com.ewp.crm.repository.interfaces.NetworkTypeRepository;
import com.ewp.crm.service.interfaces.NetworkTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NetworkTypeServiceImpl implements NetworkTypeService {

    private NetworkTypeRepository networkTypeRepository;

    @Autowired
    public NetworkTypeServiceImpl(NetworkTypeRepository networkTypeRepository) {
        this.networkTypeRepository = networkTypeRepository;
    }


    @Override
    public void updateType(SocialNetworkType socialNetworkType) {
        networkTypeRepository.saveAndFlush(socialNetworkType);
    }

    @Override
    public void deleteType(Long id) {
        networkTypeRepository.delete(id);
    }

    @Override
    public void deleteType(SocialNetworkType socialNetworkType) {
        networkTypeRepository.delete(socialNetworkType);
    }

    @Override
    public void addType(SocialNetworkType socialNetworkType) {
        networkTypeRepository.saveAndFlush(socialNetworkType);
    }


    @Override
    public SocialNetworkType getByTypeName(String name) {
        return networkTypeRepository.getSocialNetworkTypeByName(name);
    }

    @Override
    public List<SocialNetworkType> getAll() {
        return networkTypeRepository.findAll();
    }

    @Override
    public SocialNetworkType getById(Long id) {
        return networkTypeRepository.findOne(id);
    }


}
