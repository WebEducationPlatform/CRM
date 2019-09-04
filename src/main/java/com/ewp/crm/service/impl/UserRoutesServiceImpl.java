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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

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
        return  userRoutesRepository.getUserByRoleUserRoutesType( userRole,  userRouteType);
    }

    @Override
    public void updateUserRoutes(List<UserRoutesDto> userRoutesDtoListist) {
        UserRoutes userRoutesFromDB = null;
        for (UserRoutesDto routesDto : userRoutesDtoListist) {
            User hrUser = userDAO.getUserById(routesDto.getUser_id());
            userRoutesFromDB = userRoutesRepository.getByUserIdAndUserRouteType(routesDto.getUser_id(),routesDto.getUserRouteType() );
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
