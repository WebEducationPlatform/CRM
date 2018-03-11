package com.ewp.crm.repository.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.repository.interfaces.ClientRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by Pahomov on 11.03.2018.
 */
@Repository
public class ClientRepositoryImpl implements ClientRepositoryCustom {

    private EntityManager entityManager;

    @Autowired
    public ClientRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Client> customQuery(String query) {
        return entityManager.createQuery(query).getResultList();
    }
}
