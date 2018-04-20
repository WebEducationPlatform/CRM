package com.ewp.crm.repository.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.repository.interfaces.PostponeClientDataRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class PostponeClientDataRepositoryImpl implements PostponeClientDataRepositoryCustom {

	private final EntityManager entityManager;


	@Autowired
	public PostponeClientDataRepositoryImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public List<Client> getPostponedClientsToActivate() {
		return entityManager.createQuery("select cl.client from PostponeClientData cl where 1 = 1  and cl.postponeDate<=now()").getResultList();
	}
}
