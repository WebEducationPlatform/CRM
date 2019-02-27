package com.ewp.crm.service.impl;

import com.ewp.crm.models.PotentialClient;
import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.service.interfaces.FileService;
import com.ewp.crm.service.interfaces.PotentialClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FileServiceImpl implements FileService{

	private final PotentialClientService potentialClientService;

	@Autowired
	public FileServiceImpl(PotentialClientService potentialClientService) {
		this.potentialClientService = potentialClientService;
	}

	@Override
	public String getAllVkIDs() {
		StringBuilder result = new StringBuilder();
		for (PotentialClient potentialClient : potentialClientService.getAllPotentialClients()) {
			for (SocialProfile socialProfile : potentialClient.getSocialProfiles()) {
				if (socialProfile.getSocialProfileType().getName().equals("vk")) {
					result.append(socialProfile.getSocialId()).append("\n");
				}
			}
		}
		return result.toString();
	}
}
