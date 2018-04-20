package com.ewp.crm.service.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.PostponeClientData;
import com.ewp.crm.repository.interfaces.PostponeClientDataRepository;
import com.ewp.crm.service.interfaces.PostponeClientDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostponeClientDataServiceImpl implements PostponeClientDataService {

	private final PostponeClientDataRepository postponeClientDataRepository;

	@Autowired
	public PostponeClientDataServiceImpl(PostponeClientDataRepository postponeClientDataRepository) {
		this.postponeClientDataRepository = postponeClientDataRepository;
	}


	@Override
	public void add(PostponeClientData postponeClientData) {
		postponeClientDataRepository.saveAndFlush(postponeClientData);
	}

	@Override
	public void update(PostponeClientData postponeClientData) {
		postponeClientDataRepository.saveAndFlush(postponeClientData);
	}

	@Override
	public List<PostponeClientData> getAll() {
		return postponeClientDataRepository.findAll();
	}

	@Override
	public PostponeClientData get(Long id) {
		return postponeClientDataRepository.findOne(id);
	}

	@Override
	public void delete(PostponeClientData postponeClientData) {
		postponeClientDataRepository.delete(postponeClientData);
	}

	@Override
	public List<Client> getChangeActiveClients() {
		return postponeClientDataRepository.getPostponedClientsToActivate();
	}
}
