package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.SocialProfileType;

public interface SocialProfileTypeService extends CommonService<SocialProfileType>{

    SocialProfileType getByTypeName(String name);
}
