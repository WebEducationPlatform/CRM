package com.ewp.crm.repository.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.FilteringCondition;
import com.ewp.crm.repository.interfaces.ClientRepositoryCustom;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;


@Repository
public class ClientRepositoryImpl implements ClientRepositoryCustom {

    private EntityManager entityManager;

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

        boolean isWasWhere = false;

        if (filteringCondition.getSex() != null) {
            query.append(selectWhereOrAnd(isWasWhere)).append(" cl.sex = '").append(filteringCondition.getSex()).append("'");
            isWasWhere = true;
        }

        if (filteringCondition.getAgeFrom() != null || filteringCondition.getAgeTo() != null) {
            if (filteringCondition.getAgeFrom() != null && filteringCondition.getAgeTo() != null) {
                query.append(selectWhereOrAnd(isWasWhere)).append(" cl.age between ").append(filteringCondition.getAgeFrom()).append(" and ").append(filteringCondition.getAgeTo());
                isWasWhere = true;
            } else if (filteringCondition.getAgeFrom() != null) {
                query.append(selectWhereOrAnd(isWasWhere)).append(" cl.age >= ").append(filteringCondition.getAgeFrom());
                isWasWhere = true;
            } else {
                query.append(selectWhereOrAnd(isWasWhere)).append(" cl.age <= ").append(filteringCondition.getAgeTo());
                isWasWhere = true;
            }
        }

        if (!filteringCondition.getCity().equals("")) {
            query.append(selectWhereOrAnd(isWasWhere)).append(" cl.city = '").append(filteringCondition.getCity()).append("'");
            isWasWhere = true;
        }

        if (!filteringCondition.getCountry().equals("")) {
            query.append(selectWhereOrAnd(isWasWhere)).append(" cl.country = '").append(filteringCondition.getCountry()).append("'");
            isWasWhere = true;
        }

        if (filteringCondition.getDateFrom() != null || filteringCondition.getDateTo() != null) {
            if (filteringCondition.getDateFrom() != null && filteringCondition.getDateTo() != null) {
                query.append(selectWhereOrAnd(isWasWhere)).append(" cl.dateOfRegistration between '").append(filteringCondition.getDateFrom()).append("' and '").append(filteringCondition.getDateTo()).append("'");
                isWasWhere = true;
            } else if (filteringCondition.getDateFrom() != null) {
                query.append(selectWhereOrAnd(isWasWhere)).append(" cl.dateOfRegistration >= '").append(filteringCondition.getDateFrom()).append("'");
                isWasWhere = true;
            } else {
                query.append(selectWhereOrAnd(isWasWhere)).append(" cl.dateOfRegistration <= '").append(filteringCondition.getDateTo()).append("'");
                isWasWhere = true;
            }
        }

        if (filteringCondition.getState() != null) {
            query.append(selectWhereOrAnd(isWasWhere)).append(" cl.state = '").append(filteringCondition.getState()).append("'");
            isWasWhere = true;
        }

        System.out.println(query.toString());
        return query.toString();
    }

    private String selectWhereOrAnd(Boolean isWasWhere) {
        if (isWasWhere) {
            return " and";
        } else {
            return " where";
        }
    }

}
