package com.ewp.crm.repository.impl;

import com.ewp.crm.models.dto.UserRoutesDto;
import com.ewp.crm.repository.interfaces.UserRoutesRepositoryCustom;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRoutesRepositoryImpl implements UserRoutesRepositoryCustom {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    @Transactional
    public List<UserRoutesDto> getUserByRoleUserRoutesType(String userRole, String userRouteType) {
        List<UserRoutesDto> result = new ArrayList<>();

        String sqlQuery =
                " SELECT " +
                        " ur.user_routes_id as id," +
                        " u.user_id as user_id, u.first_name as first_name, u.last_name as last_name, " +
                        "ur.weight as weight, ur.user_route_type as userRouteType" +
                        " FROM  user_routes ur" +
                        " LEFT JOIN user u  on ur.user_id = u.user_id" +
                        " LEFT JOIN permissions p on p.user_id= u.user_id" +
                        " JOIN role r on  r.id = p.role_id" +
                        " WHERE r.role_name = :role" +
                        " AND ur.user_route_type = :routetype";

        List<Tuple> tuples = entityManager.createNativeQuery(sqlQuery, Tuple.class)
                .setParameter("role", userRole)
                .setParameter("routetype", userRouteType)
                .getResultList();

        for (Tuple tuple :tuples) {
            result.add(new UserRoutesDto(
                    ((BigInteger) tuple.get("user_id")).longValue(),
                    (String) tuple.get("first_name"),
                    (String) tuple.get("last_name"),
                    (Integer) tuple.get("weight"),
                    (String) tuple.get("userRouteType")
            ));
        }
        return result;
    }
}
