package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Status;
import com.ewp.crm.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClientRepository extends CommonGenericRepository<Client>, ClientRepositoryCustom {

	List<Client> getClientsByOwnerUser(User ownerUser);

	List<Client> findAllByStatus(Status status);

	Client findClientBySkype(String skypeLogin);

	Client findClientByEmail(String Email);

	Client findClientByPhoneNumber(String phoneNumber);

	List<Client> findByIdIn(List<Long> ids);

	Page<Client> findAll(Pageable pageable);

	Page<Client> findAllByOwnerUser(Pageable pageable, User clientOwner);
}
