package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.SocialNetworkType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialNetworkTypeRepository extends JpaRepository<SocialNetworkType, Long> {

	SocialNetworkType getSocialNetworkTypeByName(String name);

}
