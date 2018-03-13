package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Status;
import com.ewp.crm.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatusDAO extends JpaRepository<Status, Long> {

	Status findStatusByName(String name);

	Status findStatusByClientsIn(List<Client> users);

    List<Status> getStatusesByClientsOwnerUser(User ownerUser);

	List<Status> findAllByOrderByIdAsc();
}
