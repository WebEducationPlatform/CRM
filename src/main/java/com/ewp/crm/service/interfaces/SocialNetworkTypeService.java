package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.SocialNetworkType;

public interface SocialNetworkTypeService extends CommonService<SocialNetworkType>{
    SocialNetworkType getByTypeName(String name);
}
