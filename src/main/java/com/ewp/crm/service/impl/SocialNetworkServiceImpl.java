package com.ewp.crm.service.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.SocialNetwork;
import com.ewp.crm.repository.interfaces.SocialNetworkRepository;
import com.ewp.crm.service.interfaces.SocialNetworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SocialNetworkServiceImpl implements SocialNetworkService {

	private final SocialNetworkRepository socialNetworkRepository;

	@Autowired
	public SocialNetworkServiceImpl(SocialNetworkRepository socialNetworkRepository) {
		this.socialNetworkRepository = socialNetworkRepository;
	}

	@Override
	public SocialNetwork getSocialNetworkByLink(String link) {
		return socialNetworkRepository.getByLink(link);
	}

	@Override
	public List<SocialNetwork> getAllByClient(Client client) {
		return socialNetworkRepository.getAllByClient(client);
	}
}
