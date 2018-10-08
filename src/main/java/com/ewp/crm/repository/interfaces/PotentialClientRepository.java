package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.PotentialClient;
import com.ewp.crm.models.SocialProfile;

import java.util.List;

public interface PotentialClientRepository extends CommonGenericRepository<PotentialClient> {

	PotentialClient getPotentialClientBySocialProfiles(List<SocialProfile> list);
}
