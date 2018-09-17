package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.SocialProfile;

import java.util.List;

public interface SocialProfileService {

	SocialProfile getSocialProfileByLink(String link);

	List<SocialProfile> getAllByClient(Client client);
}
