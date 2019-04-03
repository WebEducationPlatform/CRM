package com.ewp.crm.service.impl;

import com.ewp.crm.models.ListMailingType;
import com.ewp.crm.repository.interfaces.ListMailingTypeDAO;
import com.ewp.crm.service.interfaces.ListMailingTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ListMailingTypeServiceImpl extends CommonServiceImpl<ListMailingType> implements ListMailingTypeService {

    private final ListMailingTypeDAO listMailingTypeDAO;

    @Autowired
    public ListMailingTypeServiceImpl(ListMailingTypeDAO listMailingTypeDAO) {
        this.listMailingTypeDAO = listMailingTypeDAO;
    }

    @Override
    public ListMailingType get(Long id) {
        return listMailingTypeDAO.getById(id);
    }

    @Override
    public ListMailingType get(String name) {
        return listMailingTypeDAO.getByName(name);
    }

    @Override
    public ListMailingType add(ListMailingType entity) {
        return listMailingTypeDAO.save(entity);
    }

    @Override
    public void update(ListMailingType entity) {
        listMailingTypeDAO.save(entity);
    }

    @Override
    public void delete(ListMailingType entity) {
        listMailingTypeDAO.delete(entity);
    }
}