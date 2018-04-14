package com.ewp.crm.service.impl;

import com.ewp.crm.models.SocialNetworkType;
import com.ewp.crm.repository.interfaces.SocialNetworkTypeRepository;
import com.ewp.crm.service.interfaces.SocialNetworkTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SocialNetworkTypeServiceImpl implements SocialNetworkTypeService {

    private SocialNetworkTypeRepository socialNetworkTypeRepository;

    @Autowired
    public SocialNetworkTypeServiceImpl(SocialNetworkTypeRepository socialNetworkTypeRepository) {
        this.socialNetworkTypeRepository = socialNetworkTypeRepository;
    }


    @Override
    public void updateType(SocialNetworkType socialNetworkType) {
        socialNetworkTypeRepository.saveAndFlush(socialNetworkType);
    }

    @Override
    public void deleteType(Long id) {
        socialNetworkTypeRepository.delete(id);
    }

    @Override
    public void deleteType(SocialNetworkType socialNetworkType) {
        socialNetworkTypeRepository.delete(socialNetworkType);
    }

    @Override
    public void addType(SocialNetworkType socialNetworkType) {
        socialNetworkTypeRepository.saveAndFlush(socialNetworkType);
    }


    @Override
    public SocialNetworkType getByTypeName(String name) {
        return socialNetworkTypeRepository.getSocialNetworkTypeByName(name);
    }

    @Override
    public List<SocialNetworkType> getAll() {
        return socialNetworkTypeRepository.findAll();
    }

    @Override
    public SocialNetworkType getById(Long id) {
        return socialNetworkTypeRepository.findOne(id);
    }


}
