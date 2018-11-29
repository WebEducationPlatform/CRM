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
		String result = "";
		for (PotentialClient potentialClient : potentialClientService.getAllPotentialClients()) {
			for (SocialProfile socialProfile : potentialClient.getSocialProfiles()) {
				if (socialProfile.getSocialProfileType().getName().equals("vk")) {
					Long link = socialProfile.getSocialNetworkId();
				/*	int indexOfLastSlash = link.lastIndexOf("/");
					if (indexOfLastSlash != -1) {
						link = link.substring(indexOfLastSlash + 1);
					}
					if (link.startsWith("id")) {
						link = link.replaceFirst("id", "");
					}*/
					result += "https://vk.com/id" + link + "\n";
				}
			}
		}
		return result;
	}
}
