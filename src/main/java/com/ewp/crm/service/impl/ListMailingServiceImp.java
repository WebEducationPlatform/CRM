package com.ewp.crm.service.impl;

import com.ewp.crm.models.ListMailing;
import com.ewp.crm.repository.interfaces.ListMailingDAO;
import com.ewp.crm.service.interfaces.ListMailingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ListMailingServiceImp extends CommonServiceImpl<ListMailing> implements ListMailingService {

    private final ListMailingDAO listMailingDAO;

    @Autowired
    public ListMailingServiceImp(ListMailingDAO listMailingDAO) {
        this.listMailingDAO = listMailingDAO;
    }

    @Override
    public List<ListMailing> getByType(String typeName) {
        return listMailingDAO.getByTypeName(typeName);
    }

    public void addListMailing(ListMailing listMailing) {
        listMailingDAO.save(listMailing);
    }

    @Override
    public Optional<ListMailing> getByListName(String listName) {
        return Optional.ofNullable(listMailingDAO.getByListName(listName));
    }

    public Optional<ListMailing> getListMailingById(Long id) {
        return Optional.of(listMailingDAO.getOne(id));
    }

    @Override
    public void update(ListMailing listMailing) {
        listMailingDAO.save(listMailing);
    }

    @Override
    public void delete(ListMailing listMailing) {
        listMailingDAO.delete(listMailing);
    }
}
