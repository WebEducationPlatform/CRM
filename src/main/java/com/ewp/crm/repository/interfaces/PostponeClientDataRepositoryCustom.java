package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Client;

import java.util.List;

public interface PostponeClientDataRepositoryCustom {

	List<Client> getPostponedClientsToActivate();
}
