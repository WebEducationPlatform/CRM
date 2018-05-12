package com.ewp.crm.repository.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.FilteringCondition;
import com.ewp.crm.repository.interfaces.ClientRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;


@Repository
public class ClientRepositoryImpl implements ClientRepositoryCustom {

    private final EntityManager entityManager;

    @Value("${project.jpa.batch-size}")
    private int batchSize;

    @Autowired
    public ClientRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Client> filteringClient(FilteringCondition filteringCondition) {
        return entityManager.createQuery(createQuery(filteringCondition)).getResultList();
    }

    @Override
    public List<Client> getChangeActiveClients() {
        return entityManager.createQuery("select cl from Client cl where 1 = 1 and cl.postponeDate!=NULL and cl.postponeDate<=now()").getResultList();
    }

	//TODO generics?
	@Transactional
	@Override
	public void updateBatchClients(List<Client> clients) {
		for (int i = 0; i < clients.size(); i++) {
			entityManager.merge(clients.get(i));
			if (i % batchSize == 0) {
				entityManager.flush();
				entityManager.clear();
			}
		}
		entityManager.flush();
		entityManager.clear();
	}

	private String createQuery(FilteringCondition filteringCondition) {
        StringBuilder query = new StringBuilder("select cl from Client cl where 1 = 1");

        if (filteringCondition.getSex() != null) {
            query.append(" and cl.sex = '").append(filteringCondition.getSex()).append("'");
        }

        if (filteringCondition.getAgeFrom() != null) {
            query.append(" and cl.age >= ").append(filteringCondition.getAgeFrom());

        }
        if (filteringCondition.getAgeTo() != null) {
            query.append(" and cl.age <= ").append(filteringCondition.getAgeTo());
        }

        if (!filteringCondition.getCity().isEmpty()) {
            query.append(" and cl.city = '").append(filteringCondition.getCity()).append("'");
        }

        if (!filteringCondition.getCountry().isEmpty()) {
            query.append(" and cl.country = '").append(filteringCondition.getCountry()).append("'");
        }

        if (filteringCondition.getDateFrom() != null) {
            query.append(" and cl.dateOfRegistration >= '").append(filteringCondition.getDateFrom()).append("'");
        }

        if (filteringCondition.getDateTo() != null) {
            query.append(" and cl.dateOfRegistration <= '").append(filteringCondition.getDateTo()).append("'");
        }

        if (filteringCondition.getState() != null) {
            query.append(" and cl.state = '").append(filteringCondition.getState()).append("'");
        }

        return query.toString();
    }
}