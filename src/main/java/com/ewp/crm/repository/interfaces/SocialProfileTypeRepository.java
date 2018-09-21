package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.SocialProfileType;

public interface SocialProfileTypeRepository extends CommonGenericRepository<SocialProfileType> {
	SocialProfileType getSocialProfileTypeByName(String name);
}
