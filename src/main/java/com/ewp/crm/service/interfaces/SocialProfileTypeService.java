package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.SocialProfileType;

import java.util.Optional;

public interface SocialProfileTypeService extends CommonService<SocialProfileType>{

    Optional<SocialProfileType> getByTypeName(String name);
}
