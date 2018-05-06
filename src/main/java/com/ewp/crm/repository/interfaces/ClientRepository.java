package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.SocialNetwork;
import com.ewp.crm.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long>, ClientRepositoryCustom {

	List<Client> getClientsByOwnerUser(User ownerUser);

	Client findClientByEmail(String Email);

	Client findClientByPhoneNumber(String phoneNumber);

}
