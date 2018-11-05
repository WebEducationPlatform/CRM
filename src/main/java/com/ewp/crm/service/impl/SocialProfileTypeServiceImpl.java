package com.ewp.crm.service.impl;

import com.ewp.crm.models.SocialProfileType;
import com.ewp.crm.repository.interfaces.SocialProfileTypeRepository;
import com.ewp.crm.service.interfaces.SocialProfileTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SocialProfileTypeServiceImpl extends CommonServiceImpl<SocialProfileType> implements SocialProfileTypeService {
    private SocialProfileTypeRepository socialProfileTypeRepository;

    @Autowired
    public SocialProfileTypeServiceImpl(SocialProfileTypeRepository socialProfileTypeRepository) {
        this.socialProfileTypeRepository = socialProfileTypeRepository;
    }

    @Override
    public SocialProfileType getByTypeName(String name) {
        return socialProfileTypeRepository.getSocialProfileTypeByName(name);
    }

}
