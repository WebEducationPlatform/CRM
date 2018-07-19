package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.SocialNetworkType;

public interface SocialNetworkTypeRepository extends CommonGenericRepository<SocialNetworkType> {
	SocialNetworkType getSocialNetworkTypeByName(String name);
}
