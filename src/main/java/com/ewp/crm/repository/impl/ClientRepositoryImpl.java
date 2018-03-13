package com.ewp.crm.repository.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.FilteringCondition;
import com.ewp.crm.repository.interfaces.ClientRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;


@Repository
public class ClientRepositoryImpl implements ClientRepositoryCustom {

    private EntityManager entityManager;

    private boolean isWasWhere = false;

    @Autowired
    public ClientRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Client> filteringClient(FilteringCondition filteringCondition) {
        return entityManager.createQuery(createQuery(filteringCondition)).getResultList();
    }

    private String createQuery(FilteringCondition filteringCondition) {
        StringBuilder query = new StringBuilder("select cl from Client cl");

        if (!filteringCondition.getSex().equals("")) {
            if (filteringCondition.getSex().equals(Client.Sex.MALE.name())) {
                query.append(selectWhereOrAnd()).append(" cl.sex = 'MALE'");
            } else if (filteringCondition.getSex().equals(Client.Sex.FEMALE.name())) {
                query.append(selectWhereOrAnd()).append(" cl.sex = 'FEMALE'");
            }
        }

        if (filteringCondition.getAgeFrom() != null || filteringCondition.getAgeTo() != null) {
            if (filteringCondition.getAgeFrom() != null && filteringCondition.getAgeTo() != null) {
                query.append(selectWhereOrAnd()).append(" cl.age between ").append(filteringCondition.getAgeFrom()).append(" and ").append(filteringCondition.getAgeTo());
            } else if (filteringCondition.getAgeFrom() != null) {
                query.append(selectWhereOrAnd()).append(" cl.age >= ").append(filteringCondition.getAgeFrom());
            } else {
                query.append(selectWhereOrAnd()).append(" cl.age <= ").append(filteringCondition.getAgeTo());
            }
        }

//        if (!filteringCondition.getCameFrom().equals("")) {
//            query.append(selectWhereOrAnd()).append(" cl.cameFrom = '").append(filteringCondition.getCameFrom()).append("'");
//        }

        if (!filteringCondition.getCountry().equals("")) {
            query.append(selectWhereOrAnd()).append(" cl.country = '").append(filteringCondition.getCountry()).append("'");
        }

//        if (!filteringCondition.getCity().equals("")) {
//            query.append(selectWhereOrAnd()).append(" cl.city = '").append(filteringCondition.getCity()).append("'");
//        }
//
//        if (!filteringCondition.getIsFinished().equals("")) {
//            query.append(selectWhereOrAnd()).append(" cl.isFinished = '").append(filteringCondition.getIsFinished()).append("'");
//        }
//
//        if (!filteringCondition.getIsRefused().equals("")) {
//            query.append(selectWhereOrAnd()).append(" cl.isRefused = '").append(filteringCondition.getIsRefused()).append("'");
//        }

        isWasWhere = false;
        return query.toString();
    }

    private String selectWhereOrAnd() {
        if (this.isWasWhere) {
            return " and";
        } else {
            this.isWasWhere = true;
            return " where";
        }
    }

}
