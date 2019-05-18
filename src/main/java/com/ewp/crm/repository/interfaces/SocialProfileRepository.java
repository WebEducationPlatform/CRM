package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.SocialProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialProfileRepository extends JpaRepository <SocialProfile, Long> {

	SocialProfile getBySocialIdAndSocialProfileType_Name(String socialId, String socialType);

}
