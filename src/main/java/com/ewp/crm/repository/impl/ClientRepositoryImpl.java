package com.ewp.crm.repository.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.FilteringCondition;
import com.ewp.crm.models.Status;
import com.ewp.crm.models.User;
import com.ewp.crm.repository.interfaces.ClientRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class ClientRepositoryImpl implements ClientRepositoryCustom {

    private final EntityManager entityManager;

    @Value("${project.jpa.batch-size}")
    private int batchSize;

	@Value("${project.pagination.page-size.clients}")
	private int pageSize;

    @Autowired
    public ClientRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

	@Override
	public List<Client> filteringClient(FilteringCondition filteringCondition) {
		Query query = entityManager.createQuery(createQuery(filteringCondition));
		int pageNumber = 1;
		query.setFirstResult((pageNumber - 1) * pageSize);
		query.setMaxResults(pageSize);
		List<Client> fooList = query.getResultList();
		return fooList;
	}

	public List<Client> getTimeOfSkypeCall() {
		return entityManager.createQuery("select cl from Client cl where now() BETWEEN cl.remindBeforeSkypeCall AND cl.dateOfSkypeCall").getResultList();
	}

	@Override
	public List<Client> getChangeActiveClients() {
		return entityManager.createQuery("select cl from Client cl where  cl.postponeDate is not NULL and cl.postponeDate<=now()").getResultList();
	}

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

	@Transactional
	@Override
	public void addBatchClients(List<Client> clients) {
		for (int i = 0; i < clients.size(); i++) {
			entityManager.persist(clients.get(i));
			if (i % batchSize == 0) {
				entityManager.flush();
				entityManager.clear();
			}
		}
		entityManager.flush();
		entityManager.clear();
	}

	@Override
	public List<String> getClientsEmail() {
		return entityManager.createQuery("SELECT email FROM Client").getResultList();
	}

	@Override
	public List<String> getClientsPhoneNumber() {
		return entityManager.createQuery("SELECT phoneNumber FROM Client").getResultList();
	}

	@Override
	public List<String> getFilteredClientsEmail(FilteringCondition filteringCondition) {
		return entityManager.createQuery(createQueryForGetEmails(filteringCondition)).getResultList();
	}

	@Override
	public List<String> getFilteredClientsPhoneNumber(FilteringCondition filteringCondition) {
		return entityManager.createQuery(createQueryForGetPhoneNumbers(filteringCondition)).getResultList();
	}

	@Override
	public List<String> getFilteredClientsSNLinks(FilteringCondition filteringCondition) {
		return entityManager.createNativeQuery(queryForGetSNLinksFromFilteredClients(filteringCondition)).getResultList();
	}

	@Override
	public List<Client> findByStatusAndOwnerUserOrOwnerUserIsNull(Status status, User ownUser) {
		TypedQuery<Client> query = entityManager.createQuery("from Client c where c.status = :status and (c.ownerUser in (:ownerUser) or c.ownerUser is NULL)", Client.class);
		query.setParameter("status", status);
		query.setParameter("ownerUser", ownUser);
		return query.getResultList();
	}


	private String createQuery(FilteringCondition filteringCondition) {
		return "select cl from Client cl where 1 = 1" + filterQuery(filteringCondition);
	}

	private String createQueryForGetEmails(FilteringCondition filteringCondition) {
		return "select email from Client cl where 1 = 1" + filterQuery(filteringCondition);
	}

	private String createQueryForGetPhoneNumbers(FilteringCondition filteringCondition) {
		return "select phoneNumber from Client cl where 1 = 1" + filterQuery(filteringCondition);
	}

	private String filterQuery(FilteringCondition filteringCondition) {
		StringBuilder query = new StringBuilder();

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

		if (filteringCondition.getStatus() != null) {
			query.append(" and cl.status.name = '").append(filteringCondition.getStatus()).append("')");
		}

		return query.toString();
	}

	private String queryForGetSNLinksFromFilteredClients(FilteringCondition filteringCondition) {

		StringBuilder query = new StringBuilder("SELECT social_network.link\n" +
				"FROM client_social_network\n" +
				"  INNER JOIN social_network ON client_social_network.social_network_id = social_network.id\n" +
				"  INNER JOIN client ON client_social_network.client_id = client.client_id\n" +
				"  INNER JOIN social_network_social_network_type ON social_network.id = social_network_social_network_type.social_network_id\n" +
				"  INNER JOIN social_network_type ON social_network_social_network_type.social_network_type_id = social_network_type.id\n" +
				"WHERE social_network_type.name = '" + filteringCondition.getSelected() + "'");

		if (filteringCondition.getSex() != null) {
			query.append(" and client.sex = '").append(filteringCondition.getSex()).append("'");
		}

		if (filteringCondition.getAgeFrom() != null) {
			query.append(" and client.age >= ").append(filteringCondition.getAgeFrom());

		}
		if (filteringCondition.getAgeTo() != null) {
			query.append(" and client.age <= ").append(filteringCondition.getAgeTo());
		}

		if (!filteringCondition.getCity().isEmpty()) {
			query.append(" and client.city = '").append(filteringCondition.getCity()).append("'");
		}

		if (!filteringCondition.getCountry().isEmpty()) {
			query.append(" and client.country = '").append(filteringCondition.getCountry()).append("'");
		}

		if (filteringCondition.getDateFrom() != null) {
			query.append(" and client.date >= '").append(filteringCondition.getDateFrom()).append("'");
		}

		if (filteringCondition.getDateTo() != null) {
			query.append(" and client.date <= '").append(filteringCondition.getDateTo()).append("'");
		}

		if (filteringCondition.getStatus() != null) {
			query.append(" and client.client_id in (select c2.client_id from client c2 join status_clients sc1 on sc1.user_id = c2.client_id where sc1.status_id in (select s1.status_id from status s1 where s1.status_name = '").append(filteringCondition.getStatus()).append("'))");
		}

		return query.toString();
	}
}