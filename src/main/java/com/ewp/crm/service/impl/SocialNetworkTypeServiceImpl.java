package com.ewp.crm.service.impl;

import com.ewp.crm.models.SocialNetworkType;
import com.ewp.crm.repository.interfaces.SocialNetworkTypeRepository;
import com.ewp.crm.service.interfaces.SocialNetworkTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SocialNetworkTypeServiceImpl extends CommonServiceImpl<SocialNetworkType> implements SocialNetworkTypeService {
    private SocialNetworkTypeRepository socialNetworkTypeRepository;

    @Autowired
    public SocialNetworkTypeServiceImpl(SocialNetworkTypeRepository socialNetworkTypeRepository) {
        this.socialNetworkTypeRepository = socialNetworkTypeRepository;
    }

    @Override
    public SocialNetworkType getByTypeName(String name) {
        return socialNetworkTypeRepository.getSocialNetworkTypeByName(name);
    }

}
