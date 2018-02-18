package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientDAO extends JpaRepository<Client, Long> {
	Client findClientByEmail(String Email);

	Client findClientByPhoneNumber(String phoneNumber);
}
