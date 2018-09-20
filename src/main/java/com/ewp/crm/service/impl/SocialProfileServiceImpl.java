package com.ewp.crm.service.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.repository.interfaces.SocialProfileRepository;
import com.ewp.crm.service.interfaces.SocialProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SocialProfileServiceImpl implements SocialProfileService {

	private final SocialProfileRepository socialProfileRepository;

	@Autowired
	public SocialProfileServiceImpl(SocialProfileRepository socialProfileRepository) {
		this.socialProfileRepository = socialProfileRepository;
	}

	@Override
	public SocialProfile getSocialProfileByLink(String link) {
		return socialProfileRepository.getByLink(link);
	}
}
