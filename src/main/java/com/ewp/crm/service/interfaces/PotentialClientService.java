package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.PotentialClient;
import com.ewp.crm.models.SocialProfile;

import java.util.List;
import java.util.Optional;

public interface PotentialClientService {

	List<PotentialClient> getAllPotentialClients();

	Optional<PotentialClient> getPotentialClientByID(Long id);

	void addPotentialClient(PotentialClient potentialClient);

	void updatePotentialClient(PotentialClient potentialClient);

	void deletePotentialClient(PotentialClient potentialClient);

	Optional<PotentialClient> getPotentialClientBySocialProfile(SocialProfile socialProfile);
}
