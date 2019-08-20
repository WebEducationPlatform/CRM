package com.ewp.crm.repository.impl;

import com.ewp.crm.models.dto.UserDtoForBoard;
import com.ewp.crm.repository.interfaces.UserDAOCustom;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDAOImpl implements UserDAOCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<UserDtoForBoard> getAllMentorsForDto() {
        List<UserDtoForBoard> result = new ArrayList<>();
        List<Tuple> tupleUsers = entityManager.createNativeQuery(
                "SELECT u.user_id AS id, u.first_name AS firstName, u.last_name AS lastName FROM user u " +
                        "LEFT JOIN permissions p ON p.user_id = u.user_id " +
                        "LEFT JOIN role r ON p.role_id = r.id " +
                        "WHERE r.role_name = 'MENTOR';", Tuple.class)
                .getResultList();
        for (Tuple tuple : tupleUsers) {
            long userId = ((BigInteger) tuple.get("id")).longValue();
            String firstName = tuple.get("firstName") == null ? "" : (String) tuple.get("firstName");
            String lastName = tuple.get("lastName") == null ? "" : (String) tuple.get("lastName");
            result.add(new UserDtoForBoard(userId, firstName, lastName));
        }
        return result;
    }

    @Override
    public List<UserDtoForBoard> getAllWithoutMentorsForDto() {
        List<UserDtoForBoard> result = new ArrayList<>();
        List<Tuple> tupleUsers = entityManager.createNativeQuery(
                "SELECT DISTINCT u.user_id AS id, u.first_name AS firstName, u.last_name AS lastName FROM user u " +
                "JOIN permissions p ON u.user_id not in " +
                        "(SELECT p.user_id FROM permissions p " +
                                "JOIN role r ON p.role_id = r.id " +
                                "WHERE r.role_name = 'MENTOR');", Tuple.class)
                .getResultList();
        for (Tuple tuple : tupleUsers) {
            long userId = ((BigInteger) tuple.get("id")).longValue();
            String firstName = tuple.get("firstName") == null ? "" : (String) tuple.get("firstName");
            String lastName = tuple.get("lastName") == null ? "" : (String) tuple.get("lastName");
            result.add(new UserDtoForBoard(userId, firstName, lastName));
        }
        return result;
    }
}
