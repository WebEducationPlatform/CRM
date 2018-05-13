package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.SocialNetwork;

import java.util.List;

public interface SocialNetworkService {

	SocialNetwork getSocialNetworkByLink(String link);

	List<SocialNetwork> getAllByClient(Client client);
}
