package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.models.SocialProfile.SocialNetworkType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SocialProfileRepository extends JpaRepository <SocialProfile, Long> {

	SocialProfile getBySocialIdAndSocialNetworkType(String socialId, SocialNetworkType socialNetworkType);
	List<SocialProfile> getAllBySocialNetworkType(SocialNetworkType socialNetworkType);

}
