package com.ewp.crm.service.impl;

import com.ewp.crm.models.User;
import com.ewp.crm.models.UserRoutes;
import com.ewp.crm.models.dto.UserRoutesDto;
import com.ewp.crm.repository.interfaces.UserDAO;
import com.ewp.crm.repository.interfaces.UserRoutesRepository;
import com.ewp.crm.service.interfaces.UserRoutesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import java.math.BigInteger;
import java.util.*;

@Service
public class UserRoutesServiceImpl implements UserRoutesService {

    private final UserDAO userDAO;
    private final UserRoutesRepository userRoutesRepository;
    private final EntityManager entityManager;

    @Autowired
    public UserRoutesServiceImpl( UserDAO userDAO, UserRoutesRepository userRoutesRepository, EntityManager entityManager) {
        this.userDAO = userDAO;
        this.userRoutesRepository = userRoutesRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Set<UserRoutes> getByUserId(Long userId) {
        return userRoutesRepository.getByUserId(userId);
    }

    @Override
    public UserRoutes getByUserIdAndUserRouteType(Long userId, UserRoutes.UserRouteType type) {
        return userRoutesRepository.getByUserIdAndUserRouteType(userId, type);
    }

    @Override
    public List<UserRoutes> getAllByUserRouteType(UserRoutes.UserRouteType userRouteType) {
        return userRoutesRepository.getAllByUserRouteType(userRouteType);
    }

    @Override
    public void save(UserRoutes userRoutes) {
        userRoutesRepository.save(userRoutes);
    }

    @Override
    public void saveAll(Set<UserRoutes> userRoutes) {
        userRoutesRepository.saveAll(userRoutes);
    }

    @Override
    public List<UserRoutesDto>  getUserByRoleAndUserRoutesType(String userRole, String userRouteType){
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

    @Override
    public void updateUserRoutes(List<UserRoutesDto> userRoutesDtoListist) {
        UserRoutes userRoutesFromDB = null;
        for (UserRoutesDto routesDto : userRoutesDtoListist) {
            User hrUser = userDAO.getUserById(routesDto.getUser_id());
            userRoutesFromDB = getByUserIdAndUserRouteType(routesDto.getUser_id(),routesDto.getUserRouteType() );
            if (userRoutesFromDB == null){
                UserRoutes uRoutes = UserRoutesDto.getUserRoutesFromDto(routesDto);
                uRoutes.setUser(hrUser);
                save(uRoutes);
            }else{
                userRoutesFromDB.setWeight(routesDto.getWeight());
                save(userRoutesFromDB);
            }

        }
    }

    @Override
    public Long getUserIdByPercentChance(List<UserRoutesDto> userRoutesList){
        Long[] userIds = new Long[100];
        int currentId = 0;
        int currSumm = 0;
        Collections.sort(userRoutesList,new Comparator<UserRoutesDto>() {
            public int compare(UserRoutesDto o1, UserRoutesDto o2) {
                return (o1.getWeight() < o2.getWeight()) ? -1 : 1;
            }
        });
        for (int i = 0; i < userIds.length; i++) {
            if (userRoutesList.get(currentId).getWeight() > (currSumm + i)) {
                userIds[i] = userRoutesList.get(currentId).getUser_id();
            }
            else{
                currSumm += userRoutesList.get(currentId).getWeight();
                if (currentId < userRoutesList.size()-1 ) {
                    currentId++;
                }
                userIds[i] = userRoutesList.get(currentId).getUser_id();
            }
        }
        return userIds[(int) (Math.random() * 99)];
    }
}
