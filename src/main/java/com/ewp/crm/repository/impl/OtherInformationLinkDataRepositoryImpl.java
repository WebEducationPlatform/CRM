package com.ewp.crm.repository.impl;

import com.ewp.crm.repository.interfaces.OtherInformationLinkDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Repository
public class OtherInformationLinkDataRepositoryImpl implements OtherInformationLinkDAO {
    private final EntityManager entityManager;

    @Autowired
    public OtherInformationLinkDataRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    @Override
    public void deleteOtherInformationLinkByClientId(Long id) {
        entityManager.createNativeQuery("DELETE FROM other_information_links WHERE client_id = " + id)
                .executeUpdate();
    }
}