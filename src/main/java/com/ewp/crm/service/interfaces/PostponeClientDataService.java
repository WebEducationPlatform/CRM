package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.PostponeClientData;

import java.util.List;

public interface PostponeClientDataService {

	void add(PostponeClientData postponeClientData);


	void update(PostponeClientData postponeClientData);


	List<PostponeClientData> getAll();


	PostponeClientData get(Long id);


	void delete(PostponeClientData postponeClientData);

	List<Client> getChangeActiveClients();
}
