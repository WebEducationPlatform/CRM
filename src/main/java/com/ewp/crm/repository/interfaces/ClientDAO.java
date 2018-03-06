package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientDAO extends JpaRepository<Client, Long> {

	List<Client> getClientsByOwnerUser(User ownerUser);

	Client findClientByEmail(String Email);

	Client findClientByPhoneNumber(String phoneNumber);
}
