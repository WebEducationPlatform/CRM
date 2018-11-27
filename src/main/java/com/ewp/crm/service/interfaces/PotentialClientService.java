package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.PotentialClient;
import com.ewp.crm.models.SocialProfile;

import java.util.List;

public interface PotentialClientService {

	List<PotentialClient> getAllPotentialClients();

	PotentialClient getPotentialClientByID(Long id);

	void addPotentialClient(PotentialClient potentialClient);

	void updatePotentialClient(PotentialClient potentialClient);

	void deletePotentialClient(PotentialClient potentialClient);

	PotentialClient getPotentialClientBySocialProfile(SocialProfile socialProfile);
}
