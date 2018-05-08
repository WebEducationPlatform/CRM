package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.SocialNetwork;
import com.ewp.crm.models.SocialNetworkType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialNetworkRepository extends JpaRepository <SocialNetwork, Long> {

	SocialNetwork getByLink(String ling);

}
