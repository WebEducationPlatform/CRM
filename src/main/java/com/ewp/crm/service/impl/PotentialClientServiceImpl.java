package com.ewp.crm.service.impl;

import com.ewp.crm.models.PotentialClient;
import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.repository.interfaces.PotentialClientRepository;
import com.ewp.crm.service.interfaces.PotentialClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PotentialClientServiceImpl implements PotentialClientService {

	private final PotentialClientRepository potentialClientRepository;

	@Autowired
	public PotentialClientServiceImpl(PotentialClientRepository potentialClientRepository) {
		this.potentialClientRepository = potentialClientRepository;
	}

	@Override
	public List<PotentialClient> getAllPotentialClients() {
		return potentialClientRepository.findAll();
	}

	@Override
	public Optional<PotentialClient> getPotentialClientByID(Long id) {
		return potentialClientRepository.findById(id);
	}

	@Override
	public void addPotentialClient(PotentialClient potentialClient) {
		potentialClientRepository.saveAndFlush(potentialClient);
	}

	@Override
	public void updatePotentialClient(PotentialClient potentialClient) {
		potentialClientRepository.saveAndFlush(potentialClient);
	}

	@Override
	public void deletePotentialClient(PotentialClient potentialClient) {
		potentialClientRepository.delete(potentialClient);
	}

	@Override
	public Optional<PotentialClient> getPotentialClientBySocialProfile(SocialProfile socialProfile) {
		List<SocialProfile> socialProfiles = new ArrayList<>();
		socialProfiles.add(socialProfile);
		return Optional.ofNullable(potentialClientRepository.getPotentialClientBySocialProfiles(socialProfiles));
	}
}
