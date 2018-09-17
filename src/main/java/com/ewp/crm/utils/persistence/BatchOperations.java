package com.ewp.crm.utils.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collection;

@Repository
public abstract class BatchOperations<E> {

	@Value("${project.jpa.batch-size}")
	private int batchSize;

	private final EntityManager em;

	@Autowired
	public BatchOperations(EntityManager em) {
		this.em = em;
	}

	@Transactional
	public void addBatchEntites(Collection<E> entities) {
		int i = 0;
		for (E entity : entities) {
			em.persist(entity);
			i++;
			if (i % batchSize == 0) {
				em.flush();
				em.clear();
			}
		}
		em.flush();
		em.clear();
	}

	@Transactional
	public void updateBatchEntites(Collection<E> entities) {
		int i = 0;
		for (E entity : entities) {
			em.merge(entity);
			i++;
			if (i % batchSize == 0) {
				em.flush();
				em.clear();
			}
		}
		em.flush();
		em.clear();
	}

}
