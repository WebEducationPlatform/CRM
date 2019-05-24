package com.ewp.crm.repository.impl;

import com.ewp.crm.repository.interfaces.ContractLinkDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Repository
public class ContractLinkDataRepositoryImpl implements ContractLinkDataRepository {
    private static Logger logger = LoggerFactory.getLogger(PersistentLoginRepositoryImpl.class);

    private final EntityManager entityManager;

    @Autowired
    public ContractLinkDataRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    @Override
    public void deleteContactLinkByClientId(Long id) {
        entityManager.createNativeQuery("DELETE FROM contract_links WHERE client_id = " + id)
                .executeUpdate();
        logger.info("Delete contract from client: " + id);
    }
}