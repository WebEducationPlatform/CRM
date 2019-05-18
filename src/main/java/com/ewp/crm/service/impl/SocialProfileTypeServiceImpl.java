package com.ewp.crm.service.impl;

import com.ewp.crm.models.SocialProfileType;
import com.ewp.crm.repository.interfaces.SocialProfileTypeRepository;
import com.ewp.crm.service.interfaces.SocialProfileTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SocialProfileTypeServiceImpl extends CommonServiceImpl<SocialProfileType> implements SocialProfileTypeService {
    private SocialProfileTypeRepository socialProfileTypeRepository;

    @Autowired
    public SocialProfileTypeServiceImpl(SocialProfileTypeRepository socialProfileTypeRepository) {
        this.socialProfileTypeRepository = socialProfileTypeRepository;
    }

    @Override
    public Optional<SocialProfileType> getByTypeName(String name) {
        return Optional.ofNullable(socialProfileTypeRepository.getSocialProfileTypeByName(name));
    }

}
