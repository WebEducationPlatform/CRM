package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.SocialProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SocialProfileRepository extends JpaRepository <SocialProfile, Long> {

	SocialProfile getByLink(String ling);

	List<SocialProfile> getAllByClient(Client client);
}
