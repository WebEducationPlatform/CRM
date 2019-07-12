package com.ewp.crm.service.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Role;
import com.ewp.crm.models.User;
import com.ewp.crm.repository.interfaces.UserFindTurnService;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.RoleService;
import com.ewp.crm.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Component
public class UserFindTurnServiceImpl implements UserFindTurnService {
    private final UserService userService;
    private final ClientService clientService;
    private final RoleService roleService;

    @Autowired
    public UserFindTurnServiceImpl(
            ClientService clientService,
            UserService userService,
            RoleService roleService) {
        this.clientService = clientService;
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    public User getUserToOwnCard() {
        List<Client> clients =  clientService.getAllClients();
        Role role = roleService.getRoleByName("HR");
        TreeMap<Long, Long> usersLastClient = new TreeMap<>();//карта, где ключ - id последнего взятого координатором клиента, value - id HR координатора
        List<User> users = userService.getByRole(role);//находим список всех координаторов (по роли HR)
        clients.removeIf(x->x.getOwnerUser()==null);//выбираем клинентов (карточки), у которых есть координатор
        for (User u: users) {
            List<Long> clientIds = clients.stream().filter(c->c.getOwnerUser().equals(u)).map(c->c.getId()).collect(Collectors.toList());//создаем список из id клиентов данного координатора
            usersLastClient.put(clientIds.get(clientIds.size()-1), u.getId());//добавляем ключ - id последнего взятого координатором клиента из списка его клиентов, value - id HR координатора
        }
        long userIdToGetCard = usersLastClient.firstEntry().getValue();//в результате сортировки по умолчанию в первом entry (value) содержится id координатора, которому первому(из всех координаторов) была отдана карточка нового клиента
      return userService.get(userIdToGetCard);
    }
}
