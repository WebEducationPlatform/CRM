package com.ewp.crm.service.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.repository.interfaces.ClientRepository;
import com.ewp.crm.repository.interfaces.SocialProfileRepository;
import com.ewp.crm.service.interfaces.SocialProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SocialProfileServiceImpl implements SocialProfileService {

	private final SocialProfileRepository socialProfileRepository;
	private final ClientRepository clientRepository;

	@Autowired
	public SocialProfileServiceImpl(SocialProfileRepository socialProfileRepository, ClientRepository clientRepository) {
		this.socialProfileRepository = socialProfileRepository;
		this.clientRepository = clientRepository;
	}

	@Override
	public Optional<SocialProfile> getSocialProfileBySocialIdAndSocialType(String id, String socialType) {
		return Optional.ofNullable(socialProfileRepository.getBySocialIdAndSocialProfileType_Name(id, socialType));
	}

	@Override
	public Optional<SocialProfile> getSocialProfileByClientIdAndTypeName(long clientId, String profileName) {
		Client client = clientRepository.getOne(clientId);
		Optional<SocialProfile> result = Optional.empty();
		for (SocialProfile socialProfile : client.getSocialProfiles()) {
			if (profileName.equals(socialProfile.getSocialProfileType().getName())) {
				result = Optional.of(socialProfile);
				break;
			}
		}
		return result;
	}

	@Override
	public Optional<String> getClientSocialProfileLinkByTypeName(Client client, String typeName) {
		Optional<String> result = Optional.empty();
		for (SocialProfile socialProfile : client.getSocialProfiles()) {
			if (typeName.equals(socialProfile.getSocialProfileType().getName())) {
				result = Optional.of(socialProfile.getSocialId());
				break;
			}
		}
		return result;
	}
}
