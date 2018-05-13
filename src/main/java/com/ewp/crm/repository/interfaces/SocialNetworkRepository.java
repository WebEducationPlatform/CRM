package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.SocialNetwork;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SocialNetworkRepository extends JpaRepository <SocialNetwork, Long> {

	SocialNetwork getByLink(String ling);

	List<SocialNetwork> getAllByClient(Client client);
}
